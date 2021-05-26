package com.kakaopay.investment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.investment.controller.dto.InvestmentRequest;
import com.kakaopay.investment.exception.ErrorCodeInfo;
import com.kakaopay.investment.exception.GlobalExceptionHandler;
import com.kakaopay.investment.exception.InvestmentException;
import com.kakaopay.investment.service.InvestmentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
class InvestmentControllerTests {
	private InvestmentService investmentService;

	private static final StaticApplicationContext applicationContext = new StaticApplicationContext();
	private static final WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();

	private MockMvc investmentMockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		objectMapper = new ObjectMapper();
		investmentService = Mockito.mock(InvestmentService.class);
		InvestmentRestController investmentRestController = new InvestmentRestController(investmentService);

		applicationContext.registerSingleton("exceptionHandler", GlobalExceptionHandler.class);
		webMvcConfigurationSupport.setApplicationContext(applicationContext);

		MockHttpSession session = new MockHttpSession();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		MockitoAnnotations.initMocks(this);
		investmentMockMvc = MockMvcBuilders.standaloneSetup(investmentRestController)
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
	public void 투자하기() {
		try {
			InvestmentRequest investmentRequest = new InvestmentRequest();
			investmentRequest.setProductId(1L);
			investmentRequest.setInvestingAmount(3000L);

			doNothing().when(investmentService).invest(any());
			String content = objectMapper.writeValueAsString(investmentRequest);
			System.out.println(content);
			investmentMockMvc.perform(
					MockMvcRequestBuilders
							.post("/api/invest")
							.header("X-USER-ID", 1)
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.content(content))
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}


	@Test
	public void 투자하기_파라미터누락 () {
		try {
			doNothing().when(investmentService).invest(any());

			InvestmentRequest investmentRequest = new InvestmentRequest();
			investmentRequest.setInvestingAmount(3000L);
			String content = objectMapper.writeValueAsString(investmentRequest);

			investmentMockMvc.perform(
					MockMvcRequestBuilders
							.post("/api/invest")
							.header("X-USER-ID", 1)
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.content(content))
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}


	@Test
	public void 투자하기_내부validation이슈 () {
		try {
			doThrow(new InvestmentException(ErrorCodeInfo.PRODUCT_VALIDATION)).when(investmentService).invest(any());

			InvestmentRequest investmentRequest = new InvestmentRequest();
			investmentRequest.setProductId(1L);
			investmentRequest.setInvestingAmount(3000L);
			String content = objectMapper.writeValueAsString(investmentRequest);

			investmentMockMvc.perform(
					MockMvcRequestBuilders
							.post("/api/invest")
							.header("X-USER-ID", 1)
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.content(content))
					.andExpect(status().isBadRequest())
					.andDo(print())
					.andReturn();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
