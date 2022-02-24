
package com.aerospike.examples;

import com.aerospike.client.Record;
import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.TlsPolicy;
import com.aerospike.client.policy.WritePolicy;

import java.util.Random;

public class TLSConnect {

    public static void main(String[] args) {

        // Setup debug logging in the Aerospike client to help with troubleshooting
        Log.Callback logCallback = new AerospikeLogCallback();
        Log.setCallback(logCallback);
        Log.setLevel(Log.Level.DEBUG);

        // The 'tlsName' (second argument) must be specified when using TLS. The 'tlsName' must
        // match the Common Name (CN) or a Subject Alternative Name (SAN) found in the certificate.
        Host[] hosts = new Host[] {
                new Host("ec2-54-154-191-112.eu-west-1.compute.amazonaws.com", "AllPorts", 4333)
        };

        // A 'TlsPolicy' is required when using TLS.
        ClientPolicy policy = new ClientPolicy();
        policy.tlsPolicy = new TlsPolicy();

        System.setProperty("javax.net.ssl.keyStore","src/main/resources/client.chain.p12");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");
        System.setProperty("javax.net.ssl.trustStore","src/main/resources/certificationAuthority.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");

        // By instantiating AerospikeClient, connections will be established or an exception will
        // be thrown. When using TLS, the constructor which accepts 'ClientPolicy' and 'Hosts[]' is
        // used to specify the TLS policy and "tlsName" respectively.
        AerospikeClient client = new AerospikeClient(policy, hosts);

        System.out.println("Connected successfully!");


        WritePolicy writePolicy =client.getWritePolicyDefault();
        Key key = new Key("test", "testSet", new Random().nextLong());
        client.add(writePolicy, key, new Bin[]{ new Bin("Name", "Behrad"), new Bin("Surname", "Babaee")});
        System.out.println("Write successful!");

        Record record = client.get(client.readPolicyDefault, key);
        System.out.println("Read:" + record.getString("Name") + " " + record.getString("Surname") );

        client.close();
    }
}