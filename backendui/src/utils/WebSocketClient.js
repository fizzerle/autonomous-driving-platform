import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export default class WebSocketClient {
    socket = null;
    client = null;
    subscriptions = new Map();

    connectOemCrash(oem, crashHandler) {
        this.socket = new SockJS('/notificationstorage/websocket');
        this.client = Stomp.over(this.socket);
        const that = this;
        const path = '/crash/' + oem.toLocaleLowerCase();
        this.client.connect({}, function(frame) {
            that.subscribe(path, crashHandler);
        });
    }

    connectOemEvent(oem, eventHandler) {
        this.socket = new SockJS('/eventstorage/websocket');
        this.client = Stomp.over(this.socket);
        const that = this;
        const path = '/event/' + oem.toLocaleLowerCase();
        this.client.connect({}, function(frame) {
            that.subscribe(path, eventHandler);
        });
    }

    connectBluelight(crashHandler) {
        this.socket = new SockJS('/notificationstorage/websocket');
        this.client = Stomp.over(this.socket);
        const that = this;
        this.client.connect({}, function(frame) {
            that.subscribe('/crash/bluelight', crashHandler);
        });
    }

    connectCar(crashHandler) {
        this.socket = new SockJS('/notificationstorage/websocket');
        this.client = Stomp.over(this.socket);
        const that = this;
        this.client.connect({}, function(frame) {
            that.subscribe('/crash/car', crashHandler);
        });
    }

    close() {
        const sock = this.socket;
        this.client.disconnect(function() {
            sock.close();
        });
    }

    subscribe(path, msgHandler) {
        let sub = this.client.subscribe(path, msgHandler);
        this.subscriptions.set(path, sub);
    }

};