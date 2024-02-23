# Code generation
1. Let's first create our api spec using either IDE extensions or using the [swagger hub](https://swagger.io/tools/swaggerhub/)
I am using classical `petstore` API spec which is already created by the OpenAPI Team. I put the `yaml`
file under `resources/api` folder.
2. Add the dependencies and plugin given below in addition to existing ones
```
plugins {
    //OTHER PLUGINS
    id 'org.hidetake.swagger.generator' version '2.19.2'
}
dependencies {
    /OTHER DEPENDENCIES
    compileOnly 'org.springframework.boot:spring-boot-starter-validation'
    //If we want hateoas. we also set heteoas to true in config.json
    implementation 'org.springframework.boot:spring-boot-starter-hateoas'

    //open api code generation
    swaggerCodegen 'org.openapitools:openapi-generator-cli:7.3.0'
    implementation 'io.swagger.core.v3:swagger-annotations:2.2.20'
    }
```
3. Let's now add some gradle tasks that will help using the generated code. You can find more information 
about the plugin [here](https://github.com/int128/gradle-swagger-generator-plugin).
```groovy
processResources {
    dependsOn(generateSwaggerCode)
}


swaggerSources {
    petstore {
        inputFile = file("${rootDir}/src/main/resources/api/petstore.yaml")
        code {
            language = 'spring'

            configFile = file("${rootDir}/src/main/resources/api/config.json")
            //what to generate. Optional.
            components = [models: true, apis: true, supportingFiles: 'ApiUtil.java']
            dependsOn validation //first validate the yaml
        }
    }
}

//Set generated code to sourceSet to be able to used it in the implementation
compileJava.dependsOn swaggerSources.petstore.code
sourceSets.main.java.srcDir "${swaggerSources.petstore.code.outputDir}/src/main/java"
sourceSets.main.resources.srcDir "${swaggerSources.petstore.code.outputDir}/src/main/resources"
```

4. Let's now configure our `org.hidetake.swagger.generator` plugin by creating the `config.json` file as below:
```json
{
  "library": "spring-boot",
  "dateLibrary": "java8",
  "hideGenerationTimestamp": true,
  "modelPackage": "com.example.petstore.api.model",
  "apiPackage": "com.example.petstore.api",
  "invokerPackage": "com.example.petstore.api",
  "serializableModel": true,
  "openApiNullable": false,
  "useTags": true,
  "useGzipFeature" : true,
  "hateoas": true,
  "unhandledException": true,
  "useSpringBoot3": true,
  "useSwaggerUI": true,
  "importMappings": {
    "ResourceSupport":"org.springframework.hateoas.RepresentationModel",
    "Link": "org.springframework.hateoas.Link"
  }
}
```
You can find several other options on this [page](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/spring.md#config-options--).
There are two options I want to mention:
- `useSpringBoot3`: This option is important for jakarta imports.
- `useTags`: If you don't use this option, all your endpoints will be put into one file. I am assuming that you use `tags`
  in your API spec ;)

5. It is now time to generate the code. Run the gradle command below:
```
   /gradlew clean build
```

You see that a folder `swagger-code-petstore` is generated under `build`. If you check the folder content,
you see the models and the interface of your API. You should now implement the API using these interfaces!


# Documentation
## Swagger UI
1. Add the dependency `implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'` to `gradle.build`. 
2. Add the configuration below to `application.yaml`.
   ```yaml
   springdoc:
     swagger-ui:
       path: /swagger-ui.html
   ```

3. Start the application. You could now reach to the swagger ui hitting the link `http://localhost:8080/swagger-ui.html`

## API Documentation
Let's now create the documentation. We already added the dependency above.
1. Add the api path to `application.yaml`. 
    ```yaml
    springdoc:
      api-docs:
        path: /api-docs

   ```
2. When you restart the application, you can already see that there is a documentation available on `http://localhost:8080/api-docs`
If you dont enter the configuration given at step 1, you can reach the documentation on the following path `http://localhost:8080/v3/api-docs`

You may want to create only the documentation without swagger ui. Then you need to add the dependency 
`testImplementation 'org.springdoc:springdoc-openapi-starter-webmvc-api:<version 2.x>'` and remove the one `springdoc-openapi-starter-webmvc-ui`  

## Documentation for Spring webflux
https://github.com/springdoc/springdoc-openapi?tab=readme-ov-file#spring-webflux-support-with-annotated-controllers

## Customize your description
You can customize your API description creating a bean like in `OpenAPIConfig.java`
TODO: Add link