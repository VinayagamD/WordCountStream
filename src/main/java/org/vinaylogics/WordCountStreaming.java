package org.vinaylogics;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class WordCountStreaming {

    public static void main(String[] args) throws Exception {
        // Set up the stream execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Checking input parameters
        final ParameterTool params = ParameterTool.fromArgs(args);

        // Make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);

        DataStream<String> text = env.socketTextStream("127.0.0.1", 9999);

        DataStream<Tuple2<String, Integer>> counts = text.filter((FilterFunction<String>) value -> value.startsWith("N")).map(new Tokenizer())
                .keyBy(0) // Split up the lines in pairs (2-tuples) containing: (word, 1)
                .sum(1); // group by the tuple field "0" and sum up tuple field "1"

        counts.print();
        env.execute("Streaming WordCount");
    }
}
