package chapter05;

import domain.Dictionary;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

public class UserDefineAssertionTest {

    @Test
    public void 사용자_정의_단언_추출(){
        Dictionary dict = new Dictionary();
        dict.add("A", new Long(3));
        dict.add("B", "21");

        assertCopntains(dict.iterator(), "A", 3L);
        assertCopntains(dict.iterator(), "B", "21");
    }

    private void assertCopntains(Iterator i, Object key, Object value) {
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();

            if (key.equals(entry.getKey())) {
                Assert.assertEquals(value, entry.getValue());
                return;
            }
        }
        Assert.fail("Iterator didn't contain " + key + " => " + value);
    }
}
