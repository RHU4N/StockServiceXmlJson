# Deploy — WebServicesLab

Deploy e validações executados em 27/11/2026. Ambiente demonstrativo temporário do SENAI, exclusivamente com dados fictícios.

## Arquitetura e URLs

SalesWeb (React/Vite estático na Vercel) → HTTPS/REST → StockService (Spring MVC, Java SE no Azure App Service). Produtos permanecem em memória, sem banco de dados, autenticação ou serviços adicionais.

| Componente | Endereço |
|---|---|
| Front-end | https://salesweb-xmljson.vercel.app |
| Back-end | https://stockservice-xmljson.azurewebsites.net |
| Catálogo | https://stockservice-xmljson.azurewebsites.net/produtos |
| Produto 1 | https://stockservice-xmljson.azurewebsites.net/produtos/1 |

Endpoints preservados: `GET /produtos`, `GET /produtos/{id}`, `POST /produtos`. Use `Accept: application/json` ou `application/xml`; no POST, envie também o `Content-Type` correspondente.

## Recursos e custos

| Serviço | Configuração efetiva | Custo previsto |
|---|---|---|
| Azure Resource Group | `rg-webserviceslab` | Sem custo próprio |
| Azure App Service Plan | `asp-webserviceslab-free`, Linux, **F1 / Free**, capacidade 1, East US 2 | US$ 0 no plano gratuito |
| Azure App Service | `stockservice-xmljson`, Java SE 25, HTTPS obrigatório | Incluído no F1 |
| Vercel | Projeto `salesweb-xmljson`, equipe `rhu4ns-projects`, **Hobby** confirmado pela CLI | US$ 0 dentro dos limites gratuitos |

A assinatura Azure for Students utilizada termina em `dc794a` e mantém limite de gastos habilitado. Brazil South foi bloqueada pela política regional; East US 2 é uma região permitida. O grupo conserva sua localização de metadados em Brazil South; os recursos de computação estão em East US 2.

Não havia App Service Plan reutilizável na assinatura utilizada. O plano de Functions do CEOS pertence a outra assinatura e permaneceu intacto. Nenhum recurso pago, upgrade de plano, VM, Kubernetes, gateway, banco ou monitoramento externo foi contratado.

O F1 oferece 60 minutos de CPU por dia, 1 GB de RAM e 1 GB de armazenamento, sem SLA e sem Always On. É adequado à demonstração acadêmica, com possíveis partidas frias e suspensão ao atingir cotas. Referências: [Azure F1](https://azure.microsoft.com/en-us/pricing/details/app-service/linux/), [Vercel Hobby](https://vercel.com/docs/plans/hobby).

## Versões e configuração

- Java local: 25.0.3; Azure: 25.0.5.1; Spring Boot: 4.2.0; Maven: 3.10.1 via Wrapper 3.3.4.
- Node local: 24.15.0; npm: 11.12.1; Vite travado: 8.3.8; React: 19.3.1; Axios: 1.21.1.
- Azure CLI: 2.91.0. Vercel CLI: 67.0.0 instalada localmente em uma pasta de ferramentas fora dos dois repositórios; MCP executou a configuração e integração Vercel.
- O runtime Linux `JAVA|25-java25` foi confirmado por `az webapp list-runtimes --os linux`. O `pom.xml` permaneceu em Java 25.

| Variável | Local | Produção |
|---|---|---|
| `CORS_ALLOWED_ORIGINS` | Opcional; origens locais sempre preservadas | `https://salesweb-xmljson.vercel.app,https://salesweb-xmljson-rhu4ns-projects.vercel.app,https://salesweb-xmljson-git-main-rhu4ns-projects.vercel.app` |
| `SERVER_PORT` / `PORT` | Padrão 8080 | Runtime Java da Azure fornece porta 80 |
| `JAVA_OPTS` | Opcional | `-Xms64m -Xmx384m` |
| `VITE_API_BASE_URL` | `http://localhost:8080` | `https://stockservice-xmljson.azurewebsites.net` na Vercel |

O Spring controla o CORS. Permite GET/POST/OPTIONS, os cabeçalhos de requisição necessários e expõe `Content-Type`/`Vary`, sem credenciais. Não foi habilitada uma configuração CORS paralela na Azure. Novos domínios precisam ser acrescentados explicitamente; domínios efêmeros de preview não são liberados por curinga.

## Comandos de publicação do back-end

Pré-requisitos: Azure CLI autenticado (`az login`) e JDK 25 em `JAVA_HOME`. Os comandos abaixo apontam exclusivamente ao WebServicesLab.

```powershell
$subscription = 'de7d7912-a0cf-438f-9a3a-e7c985dc794a'
az webapp list-runtimes --os linux
.\mvnw.cmd clean package
az webapp deploy --subscription $subscription `
  --resource-group rg-webserviceslab --name stockservice-xmljson `
  --src-path target/StockService-0.0.1-SNAPSHOT.jar --type jar --timeout 600000
```

Os recursos já estão criados. Na implantação inicial foram utilizados:

```powershell
az group create --subscription $subscription --name rg-webserviceslab --location brazilsouth
az appservice plan create --subscription $subscription `
  --resource-group rg-webserviceslab --name asp-webserviceslab-free `
  --location eastus2 --is-linux --sku F1 --number-of-workers 1
az webapp create --subscription $subscription --resource-group rg-webserviceslab `
  --name stockservice-xmljson --plan asp-webserviceslab-free `
  --runtime 'JAVA:25-java25' --https-only true
az webapp config appsettings set --subscription $subscription `
  --resource-group rg-webserviceslab --name stockservice-xmljson `
  --settings 'CORS_ALLOWED_ORIGINS=https://salesweb-xmljson.vercel.app,https://salesweb-xmljson-rhu4ns-projects.vercel.app,https://salesweb-xmljson-git-main-rhu4ns-projects.vercel.app' `
  'JAVA_OPTS=-Xms64m -Xmx384m' --output none
az webapp update --subscription $subscription --resource-group rg-webserviceslab `
  --name stockservice-xmljson --client-affinity-enabled false
```

Após publicar, consulte os endpoints por HTTPS. Os logs reais confirmaram Java 25, Tomcat na porta interna 80 e inicialização bem-sucedida em 4,449 segundos; veja [azure-startup.txt](evidencias-deploy/azure-startup.txt). O HTTPS termina na plataforma Azure.

## Front-end e integração Git

O projeto Vercel foi conectado ao repositório **privado** `RHU4N/SalesWebJsonXml`, branch de produção `main`. Framework Vite, Node 24.x, instalação `npm ci`, build `npm run build`, saída `dist`. A variável da API está configurada em produção e preview. Pushes futuros na `main` acionam publicação automática. Consulte também o [DEPLOY do SalesWeb](https://github.com/RHU4N/SalesWebJsonXml/blob/main/DEPLOY.md).

## Resultados comprovados

| Verificação | Resultado |
|---|---|
| `.\mvnw.cmd clean package` | 15 testes aprovados, 0 falhas/erros; JAR executável gerado |
| `npm ci`, `npm run lint`, `npm run build` | Aprovados; 0 vulnerabilidades reportadas pelo npm; bundle sem URL local da API |
| GET lista e produto 1 em JSON/XML | HTTP 200; dados equivalentes nos dois formatos |
| POST JSON e XML | HTTP 201 e dados desserializados corretamente |
| Dados inválidos JSON/XML | HTTP 400 |
| Produto inexistente | HTTP 404 |
| Accept não suportado | HTTP 406 |
| Content-Type não suportado | HTTP 415 |
| CORS | Origem Vercel aceita, preflight POST aprovado, origem desconhecida rejeitada com 403 |
| Cabeçalhos | Content-Type correto e Vary incluindo Accept; acessíveis pelo navegador |
| Collection original Postman via Newman | 12 requisições, 37 asserções, 0 falhas |
| Edge real via Playwright | 9 cenários, 12 chamadas da aplicação à API, 0 erros de CORS/Mixed Content/localhost |
| Reinício final | API HTTP 200; catálogo confirmado no terminal e navegador com os 5 produtos iniciais |

No navegador foram verificados catálogo JSON/XML, consulta por ID nos dois formatos, comparação lado a lado, bytes, cabeçalhos/status, erro 404 e POST JSON/XML. A lista inicial mede 282 bytes em JSON e 474 em XML; produto 1 mede 55 e 93 bytes. Medição do corpo UTF-8, sem cabeçalhos/compressão.

O JAR local e `/home/site/wwwroot/app.jar` na Azure têm o mesmo SHA-256:

```text
8bf3fede87ae3577716a7ccb1952d678659d40a23c5bc7a6e84016258ced9249
```

O [manifesto](evidencias-deploy/deploy-manifest.json) registra commits de implementação, deployment e artefato validados. Commits posteriores de documentação/evidências não alteram o código da aplicação; a Vercel recompila automaticamente a `main`. O back-end continua com deploy manual do JAR, sem pipeline adicional.

## Postman e evidências

Os ambientes **WebServicesLab — Local** e **WebServicesLab — Produção** foram criados na mesma workspace pessoal da [collection existente](https://go.postman.co/collection/31219875-a01c4467-c25a-49ce-83f9-1583edb30a5f). Nenhuma requisição foi removida ou alterada. Selecione o ambiente Produção antes de executar; a variável de ambiente `baseUrl` substitui o valor local da collection.

Os arquivos importáveis estão em [evidencias-deploy/](evidencias-deploy/), junto da cópia da collection, relatórios JSON/JUnit do Newman, respostas JSON/XML, validações HTTP e confirmação após reinício. Capturas reais do domínio público estão no [SalesWeb](https://github.com/RHU4N/SalesWebJsonXml/tree/main/evidencias-deploy).

Não foram capturadas as interfaces autenticadas dos portais Azure/Vercel nem a GUI do Postman. A evidência dessas operações é o resultado real de CLI/API/Newman. Nenhuma tela de provedor foi simulada.

Para reproduzir a collection com Newman instalado:

```powershell
newman run evidencias-deploy/StockService.postman_collection.json `
  -e evidencias-deploy/WebServicesLab-Producao.postman_environment.json
```

Esse teste cadastra produtos; reinicie a API ao terminar.

## Reiniciar e demonstrar

```powershell
az webapp restart --subscription de7d7912-a0cf-438f-9a3a-e7c985dc794a `
  --resource-group rg-webserviceslab --name stockservice-xmljson
```

O comando retorna antes de a troca de processo terminar. Aguarde a inicialização e confirme `GET /produtos` com cinco itens; uma resposta imediata ainda pode vir do processo anterior. Acesse o SalesWeb alguns minutos antes da apresentação para aquecer o serviço. Mostre catálogo, JSON/XML, ID 1, comparação, POST e ID inexistente. Ao finalizar os cadastros, reinicie para restaurar o catálogo.

## Interromper ou remover após a apresentação

Para parar temporariamente somente esta API:

```powershell
az webapp stop --subscription de7d7912-a0cf-438f-9a3a-e7c985dc794a `
  --resource-group rg-webserviceslab --name stockservice-xmljson
# Retomar: mesmo comando com start no lugar de stop.
```

Para remover definitivamente os recursos Azure, primeiro confira o grupo exclusivo e depois execute a exclusão:

```powershell
az resource list --subscription de7d7912-a0cf-438f-9a3a-e7c985dc794a `
  --resource-group rg-webserviceslab --output table
az group delete --subscription de7d7912-a0cf-438f-9a3a-e7c985dc794a `
  --name rg-webserviceslab
```

Na Vercel, exclua apenas `salesweb-xmljson` em Settings ou, com a CLI autenticada, execute `vercel project delete salesweb-xmljson --scope rhu4ns-projects` e confirme o nome. A exclusão não foi executada nesta entrega. Os repositórios GitHub permanecem privados e preservados.

## Limitações

A API é pública e sem autenticação, destinada exclusivamente à demonstração. Os dados são voláteis e compartilhados por todos os visitantes; qualquer POST altera o catálogo até o próximo reinício. Há uma única instância, sem escalonamento. Planos gratuitos impõem cotas e partidas frias; a disponibilidade futura depende dessas cotas e das contas Azure/Vercel. Duas assinaturas Azure antigas pediram nova autenticação; não foram necessárias para este deploy.
