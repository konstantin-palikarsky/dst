package dst.ass2.service.auth.impl;


import com.google.rpc.Code;
import com.google.rpc.Status;
import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.api.auth.proto.*;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

import javax.inject.Inject;

public class AuthServiceGrpcImpl extends AuthServiceGrpc.AuthServiceImplBase {

    @Inject
    private IAuthenticationService authenticationService;

    @Override
    public void validateToken(TokenValidationRequest request,
                              StreamObserver<TokenValidationResponse> responseObserver) {


        TokenValidationResponse response = TokenValidationResponse.newBuilder()
                .setValid(authenticationService.isValid(request.getToken()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void authenticate(AuthenticationRequest request,
                             StreamObserver<AuthenticationResponse> responseObserver) {
        try {
            var authToken = authenticationService.authenticate(request.getEmail(), request.getPassword());
            AuthenticationResponse response = AuthenticationResponse.newBuilder()
                    .setToken(authToken)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            //Auth exceptions should be opaque
            Status status = Status.newBuilder()
                    .setCode(Code.NOT_FOUND.getNumber())
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }
}
