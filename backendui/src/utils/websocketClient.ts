import SockJs from 'sockjs-client';
import Stomp from 'stompjs';

export default class WebSocketClient {
    private socket: WebSocket;
    private client: any;

    public connect() {
        this.socket = new SockJs('/eventstorage/websocket');
        this.client = Stomp.over(this.socket);
        this.client.connect({}, function(frame) {
            this.client.subscribe('/event/audi', function(msg) {
                console.info("Socket received", msg);
            });
        });
    }
}