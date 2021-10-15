package software.amazon.lightsail.database;

import lombok.val;
import software.amazon.awssdk.services.lightsail.LightsailClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.lightsail.database.helpers.handler.DatabaseHandler;
import software.amazon.lightsail.database.helpers.handler.ResourceHandler;
import software.amazon.lightsail.database.helpers.handler.TagsHandler;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected TagsHandler getTagHandler(final AmazonWebServicesClientProxy proxy,
                                            final ResourceHandlerRequest<ResourceModel> request, final CallbackContext callbackContext,
                                            final ProxyClient<LightsailClient> proxyClient, final Logger logger) {
        return new TagsHandler(proxy, callbackContext, request.getDesiredResourceState(), logger,
                proxyClient, request);
    }

    protected ResourceHandler getDatabaseHandler(final AmazonWebServicesClientProxy proxy,
                                            final ResourceHandlerRequest<ResourceModel> request, final CallbackContext callbackContext,
                                            final ProxyClient<LightsailClient> proxyClient, final Logger logger) {
        return new DatabaseHandler(proxy, callbackContext, request.getDesiredResourceState(), logger,
                proxyClient, request);
    }

    protected ReadHandler getReadHandler() {
        return new ReadHandler();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                          final ResourceHandlerRequest<ResourceModel> request, final CallbackContext callbackContext,
                                                                          final ProxyClient<LightsailClient> proxyClient, final Logger logger) {

        this.logger = logger;

        val tagsHandler = getTagHandler(proxy, request, callbackContext, proxyClient, logger);
        val databaseHandler = getDatabaseHandler(proxy, request, callbackContext, proxyClient, logger);

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(databaseHandler::handleUpdate).then(tagsHandler::handleUpdate).then(progress -> getReadHandler()
                        .handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }
}
