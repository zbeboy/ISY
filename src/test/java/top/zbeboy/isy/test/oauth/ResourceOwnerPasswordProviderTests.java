/*
 * Copyright (ISY Team) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package top.zbeboy.isy.test.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

import top.zbeboy.isy.Application;

/**
 * @author Dave Syer
 */
@ContextConfiguration(classes=Application.class)
public class ResourceOwnerPasswordProviderTests extends AbstractResourceOwnerPasswordProviderTests {

	protected String getPassword() {
		return "n9c6197zhaoyinbe";
	}

	protected String getUsername() {
		return "863052317@qq.com";
	}
	
	@Test
	@OAuth2ContextConfiguration(JdbcResourceOwner.class)
	public void testTokenObtainedWithHeaderAuthenticationAndJdbcUser() throws Exception {
		assertEquals(HttpStatus.OK, http.getStatusCode("/rest/test"));
		int expiry = context.getAccessToken().getExpiresIn();
		assertTrue("Expiry not overridden in config: " + expiry, expiry < 1000);
	}

	static class JdbcResourceOwner extends ResourceOwner implements DoNotOverride {
		public JdbcResourceOwner(Object target) {
			super(target);
			// The other tests all use SecurityProperties which should be the parent authentication manager
			setUsername("863052317@qq.com");
			setPassword("n9c6197zhaoyinbe");
		}
	}

}
