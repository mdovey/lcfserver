/*******************************************************************************
 * Copyright (c) 2016, Matthew J. Dovey (www.ceridwen.com).
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 *     http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *    
 *   
 * Contributors:
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 *
 *     
 *******************************************************************************/
package com.ceridwen.lcf.server.frontend.servlet;

import com.ceridwen.lcf.server.core.DescriptionWebPage;
import java.io.IOException;
import java.util.Enumeration;
import java.util.ServiceConfigurationError;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;

import com.ceridwen.lcf.server.core.EntityTypes;
import com.ceridwen.lcf.server.core.config.ConfigurationLoader;
import com.ceridwen.lcf.server.core.exceptions.EXC00_LCF_Exception;
import com.ceridwen.lcf.server.core.exceptions.EXC01_ServiceUnavailable;
import com.ceridwen.lcf.server.core.exceptions.EXC04_UnableToProcessRequest;
import com.ceridwen.lcf.server.core.exceptions.EXC05_InvalidEntityReference;
import com.ceridwen.lcf.server.core.exceptions.EXC06_InvalidDataInElement;
import com.ceridwen.lcf.server.core.referencing.Referencer;
import com.ceridwen.lcf.server.core.referencing.editor.ReferenceEditor;
import com.ceridwen.lcf.server.core.responses.LCFResponse;
import com.ceridwen.lcf.server.frontend.servlet.errors.ServletExceptionMapper;
import com.ceridwen.util.xml.XmlUtilities;
import java.util.Optional;


/**
 * Servlet implementation class Servlet
 */
@WebServlet(
	name = "lcfserver",
	urlPatterns = "/*"
)

// TODO Implement JSON 
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private boolean debug;
	private Optional<String> banner;
	private Optional<String> overrideBaseUrl;

    /**
     * Default constructor. 
     */
    public Servlet() {
    }

	@Override
	public void init() throws ServletException {
		super.init();

		debug = new String("true").equalsIgnoreCase(this.getInitParameter("debug"));
		overrideBaseUrl = Optional.ofNullable(this.getInitParameter("publicURL"));
		banner = Optional.ofNullable(this.getInitParameter("banner"));
	}

	@Override
	public void destroy() {
		super.destroy();
	}
     
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */   
        @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!request.getRequestURI().contains("/lcf/1.0") ||
        request.getRequestURI().endsWith("/lcf/1.0") ||
				request.getRequestURI().endsWith("/lcf/1.0/") || 
				request.getRequestURI().endsWith("/lcf/1.0/*")) {
				
				getDescriptionWebPage(request, response);
				return;
			}
			
			RestCommand command = new RestCommand(request, response, this.getBaseUrl(request));
			
			if (command.getResource() == null) {
				throw new EXC05_InvalidEntityReference("Entity type not found", "Entity type not found", request.getRequestURI(), null);
			}

			Object resp;
			
			if (command.getParentType() == null && command.getParentId() == null) {
				if (command.getId() == null) {
					resp = command.getResource().List(null, null, command.getStartIndex(), command.getCount());
				} else {
					resp = command.getResource().Retrieve(command.getId());
				}
			} else if (command.getParentType() != null && command.getParentId() != null && command.getId() == null) {
				resp = command.getResource().List(command.getParentType().getEntityTypeCodeValue(), command.getParentId(), command.getStartIndex(), command.getCount());
			} else {
				throw new EXC04_UnableToProcessRequest("Invalid URI", "Invalid URI", request.getRequestURI(), null);
			}
			
			String body;
			try {
				body = XmlUtilities.generateXML(resp);
			} catch (JAXBException e) {
				throw new EXC01_ServiceUnavailable("Error generating response", "Error generating response", request.getRequestURI(), e);						
			}
			
			response.setStatus(200);
			setDefaultHeaders(response);
			response.getWriter().append(body);	
		} catch (EXC00_LCF_Exception | LCFResponse e) {
			handleException(request, response, e);
		} catch (Exception |  AssertionError | LinkageError | ServiceConfigurationError e) {
			handleUncaughtException(request, response, e);
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
        @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RestCommand command = new RestCommand(request, response, this.getBaseUrl(request));
	
			if (command.getResource() == null) {
				throw new EXC05_InvalidEntityReference("Entity type not found", "Entity type not found", request.getRequestURI(), null);
			}
		
			Object resp;
			
			if (command.getId() == null) {
				try {
					Object entity = XmlUtilities.processXML(request.getInputStream(), command.getResource().entityType.getTypeClass());
					if (command.getParentType() == null && command.getParentId() == null) {
						resp = command.getResource().Create(null, null, entity);
					} else if (command.getParentType() != null && command.getParentId() != null) { 
						resp = command.getResource().Create(command.getParentType().getEntityTypeCodeValue(), command.getParentId(), entity);
					} else {
						throw new EXC04_UnableToProcessRequest("Invalid URI", "Invalid URI", request.getRequestURI(), null);
					}
				} catch (JAXBException e) {
					throw new EXC06_InvalidDataInElement("Invalid XML", "InvalidXML", request.getRequestURI(), e);
				}
			} else {
				throw new EXC04_UnableToProcessRequest("Invalid URI", "Invalid URI", request.getRequestURI(), null);
			}

			String body;
			try {
				body = XmlUtilities.generateXML(resp);
			} catch (JAXBException e) {
				throw new EXC01_ServiceUnavailable("Error generating response", "Error generating response", request.getRequestURI(), e);						
			}
	
			response.setStatus(201);
			setDefaultHeaders(response);
			response.getWriter().append(body);
		} catch (EXC00_LCF_Exception | LCFResponse e) {
			handleException(request, response, e);
		} catch (Exception |  AssertionError | LinkageError | ServiceConfigurationError e) {
			handleUncaughtException(request, response, e);
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
        @Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RestCommand command = new RestCommand(request, response, this.getBaseUrl(request));
			
			if (command.getResource() == null) {
				throw new EXC05_InvalidEntityReference("Entity type not found", "Entity type not found", request.getRequestURI(), null);
			}
			
			Object resp;
			if (command.getParentType() == null && command.getId() != null) {
				try {
					Object entity = XmlUtilities.processXML(request.getInputStream(), command.getResource().entityType.getTypeClass());
					resp = command.getResource().Modify(command.getId(), entity);
				} catch (JAXBException e) {
					throw new EXC06_InvalidDataInElement("Invalid XML", "InvalidXML", request.getRequestURI(), e);
				}
			} else {
				throw new EXC04_UnableToProcessRequest("Invalid URI", "Invalid URI", request.getRequestURI(), null);
			}

			String body;
			try {
				body = XmlUtilities.generateXML(resp);
			} catch (JAXBException e) {
				throw new EXC01_ServiceUnavailable("Error generating response", "Error generating response", request.getRequestURI(), e);						
			}
			
			response.setStatus(200);
			setDefaultHeaders(response);
			response.getWriter().append(body);
		} catch (EXC00_LCF_Exception | LCFResponse e) {
			handleException(request, response, e);
		} catch (Exception |  AssertionError | LinkageError | ServiceConfigurationError e) {
			handleUncaughtException(request, response, e);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
        @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RestCommand command = new RestCommand(request, response, this.getBaseUrl(request));
				
			if (command.getResource() == null) {
				throw new EXC05_InvalidEntityReference("Entity type not found", "Entity type not found", request.getRequestURI(), null);
			}
			
			if (command.getParentType() == null && command.getId() != null) {
				command.getResource().Delete(command.getId());
			} else {
				throw new EXC04_UnableToProcessRequest("Invalid URI", "Invalid URI", request.getRequestURI(), null);
			}
			
			response.setStatus(204);
			setDefaultHeaders(response);
		} catch (EXC00_LCF_Exception | LCFResponse e) {
			handleException(request, response, e);
		} catch (Exception |  AssertionError | LinkageError | ServiceConfigurationError e) {
			handleUncaughtException(request, response, e);
		}
	}

	private void handleUncaughtException(HttpServletRequest request, HttpServletResponse response, Throwable e) {
		ServletExceptionMapper mapper = new ServletExceptionMapper();
		this.handleException(request, response, mapper.mapToLcfException(e));
	}

	void handleException(HttpServletRequest request, HttpServletResponse response, Object exception) {
		int status = 500;
		Object resp = null;
    
		if (exception instanceof EXC00_LCF_Exception) {
			status = ((EXC00_LCF_Exception)exception).getHTTPErrorCode();
			resp = ((EXC00_LCF_Exception)exception).getLcfException();
		}

		if (exception instanceof LCFResponse) {
			status = ((LCFResponse)exception).getHTTPStatus();
			resp = ((LCFResponse)exception).getLCFResponse();
			ReferenceEditor referenceEditor = ConfigurationLoader.getConfiguration().getReferenceEditor();
			if (referenceEditor != null) {
				referenceEditor.init(this.getBaseUrl(request) + EntityTypes.LCF_PREFIX + "/");
				resp = Referencer.factory(resp, referenceEditor).reference();
			}			
		}
		
		try {
			String body = XmlUtilities.generateXML(resp);
			response.setStatus(status);
			response.getWriter().append(body);
		} catch (JAXBException | IOException e) {
			response.setStatus(status);
		}    	
  }  
	
	void setDefaultHeaders(HttpServletResponse response) {
    if (banner.isPresent()) {
			response.setHeader("X-Powered-By", banner.get());
		} else {
      response.setHeader("X-Powered-By", this.getClass().getAnnotation(WebServlet.class).displayName());
    }
	}

	private void getDescriptionWebPage(HttpServletRequest request, HttpServletResponse response) {
    try {
			response.setStatus(200);
			response.getWriter().append(DescriptionWebPage.getHtml(this.getBaseUrl(request)));
		} catch (IOException e) {
			response.setStatus(404);
			e.printStackTrace();
		}
	}
  
  private String getBaseUrl(HttpServletRequest request) {
    String baseUrl;
    
    if (overrideBaseUrl.isPresent()) {
      baseUrl = overrideBaseUrl.get();
    } else {
      String url = request.getRequestURL().toString();
      baseUrl = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath();
    }
    return baseUrl;    
  }
}
