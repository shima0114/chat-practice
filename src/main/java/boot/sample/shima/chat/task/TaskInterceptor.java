package jp.co.commerce.sample.shima.chat.task;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TaskInterceptor {

    @Value("${batch.name.host}")
    String batchHostName;

    @Around("execution(* jp.co.commerce.sample.shima.chat.task.taskjob..*.*(..)) && @annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        if (!allowedExecuteHost()) {
            System.out.println(String.format("host is not allowed execute. [localhost=%s, batchhost=%s]", getHostName(), batchHostName));
            return null;
        }
        String methodName = pjp.getSignature().getName();
        String currThread = Thread.currentThread().getName();
        System.out.println(String.format("schedule run [method=%s, thread=%s]", methodName, currThread));
        try {
            return pjp.proceed();
        } catch (Exception e) {
            return null;
        } finally {
            System.out.println(String.format("schedule end [method=%s, thread=%s]", methodName, currThread));
        }
    }

    private boolean allowedExecuteHost() {
        if (getHostName().equals(batchHostName)) {
            return true;
        }
        return false;
    }

    private Object getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

}
