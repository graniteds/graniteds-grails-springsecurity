package org.granite.grails.security;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.web.context.request.RequestContextHolder;


public class GrailsSpringSecurity3MetadataSourceWrapper implements FilterInvocationSecurityMetadataSource {
	
	private FilterInvocationSecurityMetadataSource wrappedMetadataSource;
	
	public void setWrappedMetadataSource(FilterInvocationSecurityMetadataSource metadataSource) {
		this.wrappedMetadataSource = metadataSource;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		if (RequestContextHolder.getRequestAttributes() == null)
			return null;
		
		return wrappedMetadataSource.getAllConfigAttributes();
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if (RequestContextHolder.getRequestAttributes() == null)		
			return null;

		return wrappedMetadataSource.getAttributes(object);		
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return wrappedMetadataSource.supports(clazz);
	}

}
