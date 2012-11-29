package com.janrain.io

import com.janrain.io.apps.module.BaseModule
import com.janrain.io.apps.stereotype.Replicant
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

@GrabResolver(name = 'janrain', root = 'https://repository-janrain.forge.cloudbees.com/release')
@Grapes([
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.2'),
@Grab(group = 'com.janrain.io', module = 'io-core', version = '0.0.3')
])
class SampleReplicantModule extends BaseModule<Replicant> implements Replicant {

    @Override
    void replicate(Map<String, Object> entity, Map<String, Object> params) {
        println "replicating $entity.email to $params.destination_folder"
    }

    @Override
    void boot(Map params) {
        println "booting $params"
    }

    @Override
    void cleanup() {
        println "cleaning up"
    }

    public static void main(String[] args) {
        // instantiate the module
        Replicant mod = new SampleReplicantModule()

        // boot your module with a set of properties
        // that you will use throughout your module by calling props['hello']
        mod.boot(["hello": "world"])

        // let's call capture and use a real world entity
        // docs: http://developers.janrain.com/documentation/api-methods/capture/entity/find/
        def http = new HTTPBuilder('https://io.dev.janraincapture.com')

        http.request(Method.POST, ContentType.JSON) {
            uri.path = "/entity.find"
            uri.query = [
                    filter: "uuid is not null",
                    max_results: 1,
                    type_name: "user",
                    client_id: "vsyzav9f9wq8u7xjvwkwhtz7kgg7dy6y",
                    client_secret: "sppfwb2cbwpfauch3xm58re7nmeuex8h"
            ]
            response.success = { resp, json ->
                // let's collect the first result and use it
                def entity = json.results[0]

                // pass in some extra contextual parameters like the destination folder
                mod.replicate(entity, ["destination_folder": "my_folder"])
            }
        }

        // this will execute after all your operations are complete
        mod.cleanup()
    }


}