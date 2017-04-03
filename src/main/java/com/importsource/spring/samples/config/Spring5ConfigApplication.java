package com.importsource.spring.samples.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.*;

/**
 * @author hezhuofan
 */
@SpringBootApplication
//@ComponentScan
//@Configuration
public class Spring5ConfigApplication {

    //通过实现BeanDefinitionRegistryPostProcessor来注册bean
    //@Component
    public static class  MyBRPP implements BeanDefinitionRegistryPostProcessor{

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
            //bar service
            beanDefinitionRegistry.registerBeanDefinition("barService", genericBeanDefinition(BarService.class).getBeanDefinition());

            //foo service
            beanDefinitionRegistry.registerBeanDefinition("fooService", genericBeanDefinition(FooService.class, () -> {
                BeanFactory beanFactory= BeanFactory.class.cast(beanDefinitionRegistry);
                return new FooService(beanFactory.getBean(BarService.class));
            }).getBeanDefinition());

        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

        }
    }


	public static void main(String[] args) {
		SpringApplication.run(Spring5ConfigApplication.class, args);

        //ApplicationContext ac=new AnnotationConfigApplicationContext(Spring5ConfigApplication.class);
	}
}

//@Component
class FooService{
    private  final  BarService barService;

    public FooService(BarService barService){
        this.barService=barService;
    }
}


//@Component  //注释掉@Component
class BarService{

}

class ProgrammaticBeanDefinitionInitializr implements ApplicationContextInitializer<GenericApplicationContext>{

    @Override
    public void initialize(GenericApplicationContext genericApplicationContext) {
        //System.err.println("Hello ,initializr");
        genericApplicationContext.registerBean(BarService.class);
        genericApplicationContext.registerBean(
                FooService.class,
                ()-> new FooService(genericApplicationContext.getBean(BarService.class))
        );
    }
}
