/*
 * Copyright (ISY Team) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package top.zbeboy.isy.test.oauth;

import org.springframework.test.context.ContextConfiguration;

import top.zbeboy.isy.Application;

/**
 * @author Ryan Heaton
 * @author Dave Syer
 */
@ContextConfiguration(classes=Application.class)
public class RefreshTokenSupportTests extends AbstractRefreshTokenSupportTests {
	protected String getPassword() {
		return "n9c6197zhaoyinbe";
	}

	protected String getUsername() {
		return "863052317@qq.com";
	}
}
