/*
 * Copyright 2010 Bruno de Carvalho
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.factor45.jhcb.benchmark;

import org.factor45.hotpotato.client.connection.factory.PipeliningHttpConnectionFactory;
import org.factor45.hotpotato.client.factory.DefaultHttpClientFactory;
import org.factor45.hotpotato.util.HostPortAndUri;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class HotpotatoPipeliningBenchmark extends HotpotatoBenchmark {

    // constants ------------------------------------------------------------------------------------------------------
    
    public HotpotatoPipeliningBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }

    // HotpotatoBenchmark ---------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();

        // Apart from pipelining, everything is set to defaults (only 3 connections per host!)
        DefaultHttpClientFactory factory = new DefaultHttpClientFactory();
        factory.setConnectionFactory(new PipeliningHttpConnectionFactory());
        this.client = factory.getClient();

        if (!this.client.init()) {
            throw new IllegalStateException("Could not initialise HttpClient");
        }

        this.target = HostPortAndUri.splitUrl(this.url);
        this.request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.target.getUri());
    }
}
