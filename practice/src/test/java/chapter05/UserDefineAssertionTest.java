package chapter05;

import domain.Counter;
import domain.Dictionary;
import domain.Employee;
import domain.GroupStub;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void 동시접근테스트() throws Exception {
        final Counter counter = new Counter();
        final int numberOfThreads = 10;

        final CountDownLatch allThreadsComplete = new CountDownLatch(numberOfThreads);

        final int callsPerThread = 100;
        final Set<Long> values = new HashSet<>();
        Runnable runnable = () -> {
            for (int i = 0; i < callsPerThread; i++) {
                values.add(counter.getAndIncrement());
            }
            allThreadsComplete.countDown();
        };

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(runnable).start();
        }

        allThreadsComplete.await(10, TimeUnit.SECONDS);

        int expectedNoOfValues = numberOfThreads * callsPerThread;
        Assert.assertEquals(expectedNoOfValues, values.size());
    }

    private GroupStub group = new GroupStub();

    @Test
    public void groupShouldContainTwoSupervisors() {
        List<Employee> all = group.list();
        List<Employee> employees = new ArrayList<>(all);
        Iterator<Employee> i = employees.iterator();
        while (i.hasNext()) {
            Employee employee = i.next();
            if (!employee.isSupervisor()) {
                i.remove();
            }
        }

        Assert.assertEquals(2, employees.size());
    }

    @Test
    public void groupShouldContainFiveNewcomers() {
        List<Employee> newComers = new ArrayList<>();
        for (Employee employee : group.list()) {
            LocalDateTime oneYearAgo = getOneYearAgo();
            if (employee.startingDate().isAfter(oneYearAgo)) {
                newComers.add(employee);
            }
        }

        Assert.assertEquals(5, newComers.size());
    }

    private LocalDateTime getOneYearAgo() {
        return LocalDateTime.of(2019, 1, 19, 0, 0);
    }
}
