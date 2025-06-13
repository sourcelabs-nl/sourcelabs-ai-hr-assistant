package nl.sourcelabs.sourcechat.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Serve React build files
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
        
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/public/")
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        // Forward root to index.html
        registry.addViewController("/")
            .setViewName("forward:/index.html")
    }
}