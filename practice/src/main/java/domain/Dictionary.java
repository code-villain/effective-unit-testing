package domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Data
public class Dictionary{

    private Map<Object, Object> dictionary;

    public Dictionary() {
        this.dictionary = new HashMap<Object, Object>();
    }

    public void add(String key, Object value){
        dictionary.put(key, value);
    }

    public Iterator iterator(){
        return dictionary.entrySet().iterator();
    }
}
