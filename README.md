#Build an IO Connector with Groovy

###Introduction:

An IO Connector is code that extends Janrain Capture functionality that would execute on certain user events, for example,  Registration, Profile Update etc.

For a given Capture instance, there can be many connectors installed, hence the concept of modules.

The sample provided implements an "Informant" module which is a stereotype for modules that integrates with Email Service providers. Typical oprations would be to subscribe or unsubscribe a user, or to sync data between Capture and the ESP.

There are many more stereotypes, we will add samples as we add new ones.

###Getting Started: 

Install groovy on Mac using homebrew:

	brew install groovy

Clone this repo

	git clone git@github.com:benjanrain/io-connector-sample.git

Run it

	groovy -Dgroovy.grape.report.downloads=true sample_informant_module.groovy

Fetch real entities from Capture see
[Capture Documentation](http://developers.janrain.com/documentation/api-methods/capture/)

	http.request(Method.POST, ContentType.JSON) {
      uri.path = "/entity.find"
      uri.query = [   
                    filter: "uuid is not null", 
                    max_results: 1, 
                    type_name: "user",
                    client_id: "_client_id_",
                    client_secret: "_client_secret_"
                  ]
     response.success = { resp, json ->
     // let's collect the first result and use it
     	def entity = json.results[0]
     }
	}

Add any necessary Grapes see [Grape Docs](http://groovy.codehaus.org/Grape)

	@Grapes(
		@Grab(group='com.amazonaws', module='aws-java-sdk', version='1.3.22')
	)
            
###Getting Support:
If you are ready to deploy and wish to submit your app, or need assistance with your module:

Email Us: [io-apps@janrain.com](mailto:io-apps@janrain.com)
