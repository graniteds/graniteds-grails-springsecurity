package org.granite.grails.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.granite.context.GraniteContext;
import org.granite.messaging.service.security.AbstractSecurityContext;
import org.granite.messaging.service.security.AbstractSpringSecurityInterceptor;
import org.granite.messaging.webapp.HttpGraniteContext;
import org.springframework.security.intercept.web.FilterInvocation;

import flex.messaging.messages.Message;
import flex.messaging.messages.RemotingMessage;


public class GrailsAcegiInterceptor extends AbstractSpringSecurityInterceptor {
	
	public FilterInvocation buildFilterInvocation(AbstractSecurityContext securityContext) {
		return new AcegiFilterInvocationAdapter(securityContext);
	}
	
	public static class AcegiFilterInvocationAdapter extends FilterInvocation {
		
		private HttpServletRequest wrappedRequest;
		
		public AcegiFilterInvocationAdapter(AbstractSecurityContext securityContext) {
			super(((HttpGraniteContext)GraniteContext.getCurrentInstance()).getRequest(), 
					((HttpGraniteContext)GraniteContext.getCurrentInstance()).getResponse(), new DummyChain());
			
			this.wrappedRequest = new SecurityRequestWrapper(((HttpGraniteContext)GraniteContext.getCurrentInstance()).getRequest(), securityContext);
		}
		
		@Override
		public HttpServletRequest getHttpRequest() {
			return wrappedRequest;
		}
	}
	
	public static class SecurityRequestWrapper extends HttpServletRequestWrapper {
		
		private AbstractSecurityContext securityContext; 
		
		public SecurityRequestWrapper(HttpServletRequest request, AbstractSecurityContext securityContext) {
			super(request);
			this.securityContext = securityContext;
		}

		@Override
		public String getRequestURI() {
			Message message = securityContext.getMessage();
			if (message instanceof RemotingMessage && "invokeComponent".equals(((RemotingMessage)message).getOperation())) {
				Object[] body = (Object[])message.getBody();
				String componentName = (String)body[0];
				if (componentName.endsWith("Controller")) {
					componentName = componentName.substring(0, componentName.length()-"Controller".length());
					int idx = componentName.lastIndexOf('.');
					if (idx > 0)
						componentName = componentName.substring(idx+1);
				}
				String methodName = (String)body[2];
				return super.getContextPath() + "/" + componentName + "/" + methodName;
			}
			
			return super.getRequestURI();
		}
	}
	
	public static class DummyChain implements FilterChain {

		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		}
		
	}
}
