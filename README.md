# openapi generator for JeKa

```properties
jeka.cmd._append=@dev.jeka:openapi-plugin:0.10.24.1 #openapi

openapi#cliVersion=7.0.1

openapi.gen.myServer=generate -g spring \
  --model-name-prefix Rest \
  -i https://petstore.swagger.io/v2/swagger.json \
  --additional-properties=useBeanValidation=true,useSwaggerUI=false,interfaceOnly=true

openapi.gen.myClient=generate -g client \
  --model-name-prefix Rest \
  -i https://my.spec.server/an-api.json
```






