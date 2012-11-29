package com.janrain.io

import com.google.common.base.Preconditions
import com.janrain.io.apps.module.BaseModule
import com.janrain.io.apps.stereotype.Messenger
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.commons.validator.EmailValidator

@GrabResolver(name = 'janrain', root = 'https://repository-janrain.forge.cloudbees.com/release')
@Grapes([
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.2'),
@Grab(group = 'com.google.guava', module = 'guava', version = '13.0.1'),
@Grab(group = 'commons-validator', module = 'commons-validator', version = '1.4.0'),
@Grab(group = 'com.janrain.io', module = 'io-core', version = '0.0.3')
])
class SampleMessengerModule extends BaseModule<Messenger> implements Messenger {

    @Override
    void sendMessage(Map<String, Object> params) {

        Preconditions.checkNotNull(params.to, "sendMessage %s argument missing", "to")
        Preconditions.checkNotNull(params.from, "sendMessage %s argument missing", "from")
        Preconditions.checkNotNull(params.subject, "sendMessage %s argument missing", "subject")
        Preconditions.checkNotNull(params.body, "sendMessage %s argument missing", "body")
        Preconditions.checkArgument(EmailValidator.instance.isValid(params.to), "invalid email address: %s", params.to)
        Preconditions.checkArgument(EmailValidator.instance.isValid(params.from), "invalid email address: %s", params.from)

        println "sending email to $params.to"
    }

    @Override
    void boot(Map params) {
        println "booting $params"
    }

    @Override
    void cleanup() {
        println "cleaning up"
    }

    @Override
    boolean self_test(Map params) {
        return true
    }

    public static void main(String[] args) {
        // instantiate the module
        Messenger mod = new SampleMessengerModule()

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
                mod.sendMessage(["to": entity.email, "from": "noreply@janrain.com", "subject": "hello world", "body": "goodbye world"])
            }
        }

        // this will execute after all your operations are complete
        mod.cleanup()
    }


}