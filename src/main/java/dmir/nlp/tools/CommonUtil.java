package dmir.nlp.tools;

import org.bson.Document;
import org.codehaus.jackson.JsonNode;

import java.util.Iterator;

/**
 * Created by Richard on 2016-08-18.
 */
public class CommonUtil {
    public CommonUtil(){}

    public Document jsonNodeToBson(JsonNode node){
        Document document = new Document();
        Iterator<String> keys = node.getFieldNames();
        while(keys.hasNext()){
            String fieldName = keys.next();
            document.put(fieldName,node.path(fieldName).toString());
        }
        return document;
    }
}
