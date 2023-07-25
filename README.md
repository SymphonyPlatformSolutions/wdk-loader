# Symphony WDK Loader
This loader allows WDK to be run in API mode while deploying SWADL files once from a designated location at startup.
This allows secrets to be used in production when API-based deployments are not preferred.

## Configuration
1. Enable WDK API mode by setting `wdk.workflows.path` to an empty string and enabling the `monitoring-token` and `management-token` 
2. Set `wdk.loader.path` to the actual path of where the SWADL files should be loaded from
    ````yaml
    wdk:
      workflows.path: ""
      loader.path: ./workflows
      encrypt.passphrase: mysecretpassphrase
      properties:
        monitoring-token: mysecretpassphrase
        management-token: mysecretpassphrase
    ````
3. Add secrets via environment variables prefixed with `WDK_SECRET_` in the runtime location 
   - e.g. adding a `WDK_SECRET_secretToken` environment variable with a `abc` value will create a WDK secret at startup named `secretToken` with a value of `abc` 
