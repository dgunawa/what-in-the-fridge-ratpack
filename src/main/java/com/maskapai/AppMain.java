package com.maskapai;

import com.maskapai.model.CountingProcessor;
import com.maskapai.performance.HystrixSyncHttpCommand;
import com.maskapai.security.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.dropwizard.metrics.DropwizardMetricsModule;
import ratpack.form.Form;
import ratpack.guice.Guice;
import ratpack.handling.RequestLogger;
import ratpack.jackson.Jackson;
import ratpack.server.RatpackServer;

import java.net.URI;

public class AppMain {
    private final static Logger LOGGER = LoggerFactory.getLogger(AppMain.class);

    private static final URI uri = URI.create("https://www.myhystrixserver.com/");
    private static final int timeout = 500;

    public static void main(String[] args) throws Exception {
        CountingProcessor countingProcessor = new CountingProcessor();
        Authentication authentication = new Authentication();

        RatpackServer.start(
           server -> server
                .registry(Guice.registry(bindings -> bindings
                    .module(DropwizardMetricsModule.class, config -> config.jmx().console())
                ))
                .handlers(chain -> chain
                .all(RequestLogger.ncsa())
                .get("sync", ctx -> ctx.render(new HystrixSyncHttpCommand(uri, timeout) {}.execute()))
                .path("login", ctx -> ctx
                        .byMethod(method -> method
                                .post(() -> {
                                    ctx.parse(Form.class).then(form -> {
                                        String username = form.get("username");
                                        String password = form.get("password");
                                        if (authentication.verifyUsernamePassword(username, password)) {
                                            ctx.render(authentication.getToken(username, password));
                                        } else {
                                            ctx.clientError(401);
                                        }
                                    });
                                })
                        )
                )
                .path("refrigerator/:id", ctx -> ctx
                        .byMethod(method -> method
                                .get(() -> {
                                    if (countingProcessor.isId(ctx.getPathTokens().get("id"))) {
                                        if (authentication.verifyToken(ctx.getRequest().getHeaders().get("token"))) {
                                            ctx.render(Jackson.json(countingProcessor.getId(ctx.getPathTokens().get("id"))));
                                        } else {
                                            ctx.clientError(401);
                                        }
                                    } else {
                                        ctx.clientError(404);
                                    }
                                })
                                .delete(() -> {
                                    if (countingProcessor.isId(ctx.getPathTokens().get("id"))) {
                                        if (authentication.verifyToken(ctx.getRequest().getHeaders().get("token"))) {
                                            ctx.render(Jackson.json(countingProcessor.deleteId(ctx.getPathTokens().get("id"))));
                                        } else {
                                            ctx.clientError(401);
                                        }
                                    } else {
                                        ctx.clientError(404);
                                    }
                                })
                        )
                )
                .path("refrigerator/:id/item", ctx -> ctx
                        .byMethod(method -> method
                                .post(() -> {
                                    if (countingProcessor.isId(ctx.getPathTokens().get("id"))) {
                                        if (authentication.verifyToken(ctx.getRequest().getHeaders().get("token"))) {
                                            ctx.getRequest().getBody().then(data -> {
                                                countingProcessor.postItemJson(ctx.getPathTokens().get("id"), data.getText());
                                                ctx.render(Jackson.json(countingProcessor.getId(ctx.getPathTokens().get("id"))));
                                            });
                                        } else {
                                            ctx.clientError(401);
                                        }
                                    } else {
                                        ctx.clientError(404);
                                    }
                                })
                                .put(() -> {
                                    if (countingProcessor.isId(ctx.getPathTokens().get("id"))) {
                                        if (authentication.verifyToken(ctx.getRequest().getHeaders().get("token"))) {
                                            ctx.getRequest().getBody().then(data -> {
                                                countingProcessor.putItemJson(ctx.getPathTokens().get("id"), data.getText());
                                                ctx.render(Jackson.json(countingProcessor.getId(ctx.getPathTokens().get("id"))));
                                            });
                                        } else {
                                            ctx.clientError(401);
                                        }
                                    } else {
                                        ctx.clientError(404);
                                    }
                                })
                                .delete(() -> {
                                    if (countingProcessor.isId(ctx.getPathTokens().get("id"))) {
                                        if (authentication.verifyToken(ctx.getRequest().getHeaders().get("token"))) {
                                            ctx.getRequest().getBody().then(data -> {
                                                countingProcessor.deleteItemJson(ctx.getPathTokens().get("id"), data.getText());
                                                ctx.render(Jackson.json(countingProcessor.getId(ctx.getPathTokens().get("id"))));
                                            });
                                        } else {
                                            ctx.clientError(401);
                                        }
                                    } else {
                                        ctx.clientError(404);
                                    }
                                })
                        )
                )
                .path("refrigerator/:id/item/:itemName", ctx -> ctx
                        .byMethod(method -> method
                                .get(() -> {
                                    if (countingProcessor.isItem(ctx.getPathTokens().get("id"), ctx.getPathTokens().get("itemName"))) {
                                        if (authentication.verifyToken(ctx.getRequest().getHeaders().get("token"))) {
                                            ctx.render(countingProcessor.getItem(ctx.getPathTokens().get("id"), ctx.getPathTokens().get("itemName")));
                                        } else {
                                            ctx.clientError(401);
                                        }
                                    } else {
                                        ctx.clientError(404);
                                    }
                                })
                        )
                )
        ));
    }
}
