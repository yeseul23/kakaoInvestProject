package com.kakaopay.investment.controller;

import com.kakaopay.investment.exception.GlobalExceptionHandler;
import com.kakaopay.investment.service.MemberService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
class MemberControllerTests {
	private MemberService memberService;
	private MockMvc mockMvc;

	private static final StaticApplicationContext applicationContext = new StaticApplicationContext();
	private static final WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();

	@BeforeEach
	public void setup() {
		memberService = Mockito.mock(MemberService.class);
		MemberRestController memberRestController = new MemberRestController(memberService);

		applicationContext.registerSingleton("exceptionHandler", GlobalExceptionHandler.class);
		webMvcConfigurationSupport.setApplicationContext(applicationContext);

		MockHttpSession session = new MockHttpSession();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(memberRestController)
				.setHandlerExceptionResolvers(withExceptionControllerAdvice()).build();
	}

	private ExceptionHandlerExceptionResolver withExceptionControllerAdvice() {
		final ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
			@Override
			protected ServletInvocableHandlerMethod getExceptionHandlerMethod(final HandlerMethod handlerMethod, final Exception exception) {
				Method method = new ExceptionHandlerMethodResolver(GlobalExceptionHandler.class).resolveMethod(exception);
				if (method != null) {
					return new ServletInvocableHandlerMethod(new GlobalExceptionHandler(), method);
				}
				return super.getExceptionHandlerMethod(handlerMethod, exception);
			}
		};
		exceptionResolver.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
		exceptionResolver.afterPropertiesSet();
		return exceptionResolver;
	}

	@Test
	public void 나의_투자상품_조회 () {
		try {
			MvcResult result = mockMvc.perform(
					MockMvcRequestBuilders
							.get("/api/member/myList")
							.header("X-USER-ID", 1)
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
