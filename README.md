# CroctChallengeSolution
Repository that contains the solution to Croct Backend Code Challenges

In order to run this code, you'll need:
- [Kafka Streams](https://kafka.apache.org/documentation/streams/)
- [Maven Apache](https://maven.apache.org/download.cgi)
- Java JDK 8

## First Steps
  After following the first 2 steps on how to [Run Kafka](https://kafka.apache.org/33/documentation/streams/quickstart), lets create our input and output topic, by writting on command Prompt:
  
### For the Input Topic, write:
``` 
bin/kafka-topics.sh 
    --create 
    --bootstrap-server localhost:9092 
    --replication-factor 1 
    --partitions 1 
    --topic streams-informacaocliente-input
```

A message should appear on our command Prompt 
> Created topic "streams-informacaocliente-input".

### For the Output Topic, write:
```
bin/kafka-topics.sh 
    --create 
    --bootstrap-server localhost:9092 
    --replication-factor 1 
    --partitions 1 
    --topic streams-retornoapi-output
```

A message should appear on our command Prompt 
> Created topic "streams-retornoapi-output".

## How to use
  When you have finished creating the topics and installing Maven, import the project by opening it on your favorite IDE and write on its terminal's
```
mvn clean package
```
Now, just execute our main function and, in two separate command Prompts, let's run our producer and consumer with the topics previously created.

### For our producer, write:
```
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic streams-informacaocliente-input
```
### For our consumer, write:
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-retornoapi-output --from-beginning
```

Back to our producer, just write:

>{"id":"[CLIENT_ID]","timeStamp":"[TIME_MILLISECONDS]","clientIp":"[ANY_VALID_PUBLIC_IP_ADRESS]"} \
>//this input simulates a payload received from other microservice, with the ID of a Client \
>//The time in milliseconds since the UNIX epoch and his Public IP Adress

Lets use this, as an example:
>{'id':'145','timeStamp':'1666283233','clientIp':'187.113.21.122'}

You should see on our IDE's console the confirmation that we received the input and it's now ready to execute the request to our IpStack API. 
After a few seconds, both in consumer's command Prompt and IDE's console, we should see the response in the following format:

>{ \
>'id':'145', \
>'timeStamp':'1666283233', \
>'clientIp':'187.113.21.122', \
>'latitude':'-16.68977928161621', \
>'longitude':'-49.26816177368164', \
>'country':'Brazil', \
>'region':'Goiás', \
>'city':'Goiânia' \
>}

### That concludes our brief explanation on how to code works!

