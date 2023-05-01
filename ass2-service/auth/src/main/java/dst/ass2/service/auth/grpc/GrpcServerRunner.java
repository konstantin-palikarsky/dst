package dst.ass2.service.auth.grpc;

import dst.ass2.service.auth.impl.AuthServiceGrpcImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;

@ManagedBean
public class GrpcServerRunner implements IGrpcServerRunner {

    public GrpcServerRunner() {
    }

    @Inject
    private AuthServiceGrpcImpl authService;

    @Inject
    private GrpcServerProperties grpcServerProperties;

    @Override
    public void run() throws IOException {
        Server server = ServerBuilder
                .forPort(grpcServerProperties.getPort())
                .addService(authService).build();

        server.start();
        try {
            server.awaitTermination();
        } catch (InterruptedException ignored) {
        }

    }
}
