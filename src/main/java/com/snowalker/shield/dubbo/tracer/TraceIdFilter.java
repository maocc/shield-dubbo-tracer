package com.snowalker.shield.dubbo.tracer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;

/**
 * TraceId Dubbo过滤器
 */
@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public class TraceIdFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceIdFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        String traceId = "";
        if (rpcContext.isConsumerSide()) {
            if (StringUtils.isBlank(TraceIdUtil.getTraceId())) {
                // 根调用，生成TraceId
                traceId = TraceIdGenerator.createTraceId();
            } else {
                // 后续调用，从Rpc上下文取出并设置到线程上下文
                traceId = TraceIdUtil.getTraceId();
            }
            TraceIdUtil.setTraceId(traceId);
            MDC.put("traceId", traceId);
            RpcContext.getContext().setAttachment(TraceIdConst.TRACE_ID, TraceIdUtil.getTraceId());
        }
        if (rpcContext.isProviderSide()) {
            // 服务提供方，从Rpc上下文获取traceId
            traceId = RpcContext.getContext().getAttachment(TraceIdConst.TRACE_ID);
            TraceIdUtil.setTraceId(traceId);
            MDC.put("traceId", traceId);
        }
        Result result = invoker.invoke(invocation);
        return result;
    }
}
