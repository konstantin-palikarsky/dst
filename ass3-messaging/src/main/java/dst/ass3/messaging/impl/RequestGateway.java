package dst.ass3.messaging.impl;

import dst.ass3.messaging.IRequestGateway;
import dst.ass3.messaging.TripRequest;

import java.io.IOException;

public class RequestGateway implements IRequestGateway {
    @Override
    public void submitRequest(TripRequest request) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void close() throws IOException {
        throw new RuntimeException("Unimplemented");
    }
}
