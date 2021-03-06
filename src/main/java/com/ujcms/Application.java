package com.ujcms;

import com.ofwise.util.db.DataScriptInitializer;
import com.ofwise.util.image.ImageHandler;
import com.ofwise.util.image.ThumbnailatorHandler;
import com.ofwise.util.security.csrf.CsrfInterceptor;
import com.ofwise.util.security.jwt.JwtProperties;
import com.ofwise.util.web.DirectoryRedirectInterceptor;
import com.ofwise.util.web.PathResolver;
import com.ofwise.util.web.TimerInterceptor;
import com.ujcms.core.service.GlobalService;
import com.ujcms.core.service.SiteQueryService;
import com.ujcms.core.service.UserService;
import com.ujcms.core.support.Props;
import com.ujcms.core.support.Utils;
import com.ujcms.core.web.support.BackendInterceptor;
import com.ujcms.core.web.support.ExceptionResolver;
import com.ujcms.core.web.support.FrontendInterceptor;
import com.ujcms.core.web.support.JwtInterceptor;
import com.ujcms.core.web.support.UrlRedirectInterceptor;
import no.api.freemarker.java8.Java8ObjectWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import static com.ujcms.core.support.UrlConstants.API;
import static com.ujcms.core.support.UrlConstants.BACKEND_API;

/**
 * ?????? https://start.spring.io/ ?????????????????????
 *
 * @author PONY
 */
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {
    /**
     * UJCMS ??????
     */
    @Bean
    public Props props() {
        return new Props();
    }

    /**
     * ????????????????????????????????????????????????
     */
    @Bean
    public LiteDeviceResolver liteDeviceResolver() {
        return new LiteDeviceResolver();
    }

    /**
     * ????????????????????????
     */
    @Bean
    public PathResolver pathResolver(ServletContext servletContext) {
        return new PathResolver(servletContext);
    }

    /**
     * ??????????????????
     */
    @Bean
    public ImageHandler imageHandler() {
        return new ThumbnailatorHandler();
    }

    /**
     * ???????????????
     */
    @Bean
    @DependsOn("liquibase")
    @ConditionalOnProperty(prefix = "ujcms", name = "data-sql-enabled", matchIfMissing = true)
    public DataScriptInitializer databaseInitializer(Props props,
                                                     DataSource dataSource, ResourceLoader resourceLoader) {
        return new DataScriptInitializer(dataSource, resourceLoader, "ujcms_global", props.getDataSqlPlatform());
    }

    @Configuration
    public static class WebConfigurer implements WebMvcConfigurer, InitializingBean {
        private UserService userService;
        private SiteQueryService siteQueryService;
        private GlobalService globalService;
        private Props props;
        private DeviceResolver deviceResolver;
        private ResourceLoader resourceLoader;
        private ServletContext servletContext;
        private freemarker.template.Configuration configuration;

        public WebConfigurer(UserService userService, SiteQueryService siteQueryService, GlobalService globalService,
                             Props props, DeviceResolver deviceResolver, ResourceLoader resourceLoader,
                             ServletContext servletContext, freemarker.template.Configuration configuration) {
            this.userService = userService;
            this.siteQueryService = siteQueryService;
            this.globalService = globalService;
            this.props = props;
            this.deviceResolver = deviceResolver;
            this.resourceLoader = resourceLoader;
            this.servletContext = servletContext;
            this.configuration = configuration;
        }

        /**
         * ????????????
         */
        @Bean
        public ExceptionResolver exceptionResolver() {
            return new ExceptionResolver();
        }

        /**
         * ??????????????????
         */
        @Bean
        public TimerInterceptor timerInterceptor() {
            return new TimerInterceptor();
        }

        /**
         * JWT ??????
         */
        @Bean
        public JwtProperties jwtProperties() {
            return new JwtProperties();
        }

        /**
         * JWT ?????????
         */
        @Bean
        public JwtInterceptor jwtInterceptor() {
            return new JwtInterceptor(jwtProperties(), userService);
        }

        /**
         * CSRF ?????????
         */
        @Bean
        public CsrfInterceptor csrfInterceptor() {
            return new CsrfInterceptor();
        }

        /**
         * ???????????????
         */
        @Bean
        public BackendInterceptor backendInterceptor() {
            return new BackendInterceptor(siteQueryService, props);
        }

        /**
         * ???????????????
         */
        @Bean
        public FrontendInterceptor frontendInterceptor() {
            return new FrontendInterceptor(deviceResolver, props);
        }

        /**
         * URL???????????????
         */
        @Bean
        public UrlRedirectInterceptor urlRedirectInterceptor() {
            return new UrlRedirectInterceptor(globalService);
        }

        /**
         * ???????????????????????????????????????
         */
        @Bean
        public DirectoryRedirectInterceptor directoryRedirectInterceptor() {
            return new DirectoryRedirectInterceptor(resourceLoader, props.isFileToDir(), props.isDirToFile());
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            // ???????????????????????????????????? jquery.js bootstrap.min.css ?????????????????????
            registry.addInterceptor(timerInterceptor()).excludePathPatterns("/error/**", "/**/*.*");
            // RESTful ???????????????????????? CSRF ??????
            registry.addInterceptor(csrfInterceptor()).excludePathPatterns(API + "/**", "/error/**", "/**/*.*");
            // RESTful ??????????????? /api ??????
            registry.addInterceptor(jwtInterceptor()).addPathPatterns(API + "/**");
            // ???????????????
            registry.addInterceptor(backendInterceptor()).addPathPatterns(BACKEND_API + "/**");
            // ????????????????????????
            if (props.isFileToDir() || props.isDirToFile()) {
                registry.addInterceptor(directoryRedirectInterceptor())
                        .excludePathPatterns(API + "/**", "/error/**", "/**/*.*");
            }
            // ???????????????
            registry.addInterceptor(urlRedirectInterceptor()).excludePathPatterns(API + "/**", "/error/**", "/**/*.*");
            registry.addInterceptor(frontendInterceptor()).excludePathPatterns(API + "/**", "/error/**", "/**/*.*");
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            // ?????? api ??????
//            registry.addMapping(API + "/**").allowedOrigins("*");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // ?????????????????????????????????????????????????????????????????????1???5????????????
            // ????????????????????????????????????????????????????????????tomcat?????????????????????
            // ??????????????????????????????????????????????????????????????? spring.web.resources.static-locations ?????????
            // ?????????????????? profile ???????????????
            // List<String> profiles = Arrays.asList(applicationContext.getEnvironment().getActiveProfiles());
            String uploadsLocation = props.getUploadsLocation();
            if (StringUtils.isNotBlank(uploadsLocation)) {
                registry.addResourceHandler(uploadsLocation + "/**").
                        addResourceLocations("file:" + servletContext.getRealPath(uploadsLocation + "/"));
            }
        }

        @Override
        public void afterPropertiesSet() {
            configuration.setObjectWrapper(new Java8ObjectWrapper(freemarker.template.Configuration.VERSION_2_3_31));
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return customizerBuilder(builder);
    }

    public static void main(String[] args) {
        customizerBuilder(new SpringApplicationBuilder()).run(args);
    }

    private static SpringApplicationBuilder customizerBuilder(SpringApplicationBuilder builder) {
        Utils.boot();
        return builder.sources(Application.class);
    }
}
