package main.se.miun.dt176g.anby2001.reactive.multiplayer;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import main.se.miun.dt176g.anby2001.reactive.draw.Drawing;
import main.se.miun.dt176g.anby2001.reactive.draw.DrawingPanel;
import main.se.miun.dt176g.anby2001.reactive.shapes.Shape;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Server side
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */
public class Server {
    public class ConnectError extends Throwable {

        private static final long serialVersionUID = 1L;
        private final Socket socket;

        public ConnectError(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }
    }

    private Map<Integer, Disposable> disposables;    //key: socket hash
    private Map<Integer, Disposable> drawingDisposables;

    private Subject<Socket> connections;
    private DrawingPanel drawing;

    private Subject<Shape> drawingStream;  // all drawings

    private ServerSocket serverSocket;
    private boolean acceptConnections = true;

    /**
     * starts the server and runs until any command is entered
     */
    public Server() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            Disposable disposable = Observable.just(this)
                    .subscribeOn(Schedulers.single())
                    .doOnNext(this::run)
                    .doOnDispose(this::shutdown)
                    .doOnSubscribe(d -> System.out.println("Server is running...press <enter> to stop"))
                    .subscribe();

            br.readLine();

            disposable.dispose();
            Schedulers.shutdown();

            System.out.println("-- Shutting down --");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create containers and stores connections
     * @param server server
     */
    private void run(Server server) {
        disposables = new HashMap<>();
        drawingDisposables = new HashMap<>();
        connections = PublishSubject.create();
        drawing = new DrawingPanel();
        drawingStream = ReplaySubject.<Shape>createWithSize(3);  //replay shapes to new clients

        // listen for requests on separate thread
        Completable.create(emitter -> listenForIncomingConnectionRequests())
                .subscribeOn(Schedulers.single())
                .subscribe();

        connections
                .doOnNext(s -> System.out.println("tcp connection accepted..."))
                .subscribe(this::listenToSocket);
    }

    /**
     * listen for connections
     */
    private void listenForIncomingConnectionRequests() throws IOException {
        serverSocket = new ServerSocket(12345);

        while (acceptConnections) {
            Socket socket = serverSocket.accept();
            socketToPrintWriterLogic(socket)
                    .subscribe(e -> e.writeObject(this.drawing.getDrawing()))
                    .dispose();
            Observable.<Socket>create(emitter -> emitter.onNext(socket))
                    .observeOn(Schedulers.io())
                    .subscribe(connections);
        }

    }

    /**
     * stores incoming connections/shapes
     * sends shapes back to users
     * @param socket server socket
     */
    private void listenToSocket(Socket socket) {
        //inject all incoming messages from this socket to the message stream
        Observable.<Shape>create(emitter -> {
                    socketToBufferedReaderLogic(socket)
                            .subscribe(shape -> {
                                while(!emitter.isDisposed()) {
                                    Shape in = (Shape) shape.readObject();
                                    if (in == null || socket.isClosed()) {
                                        emitter.onError(new ConnectError(socket));
                                    }
                                    else {
                                        emitter.onNext(in);
                                    }
                                }
                            }, err -> System.err.println(err.getMessage()));
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> disposables.put(socket.hashCode(), d))
                .doOnError(this::handleError)
                .doOnNext(this.drawing::addShape)
                .onErrorComplete(err -> err instanceof ConnectError)
                .subscribe(drawingStream::onNext
                        , err -> System.err.println(err.getMessage())
                        , () -> System.out.println("Socket closed"));


        // subscribe each newly connected client to the drawingStream
        drawingStream
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> drawingDisposables.put(socket.hashCode(), d))
                .doOnError(this::handleError)
                .withLatestFrom(socketToPrintWriterLogic(socket), (m, pw) -> {
                    pw.writeObject(m);
                    pw.flush();
                    return true;
                })
                .subscribe();
    }

    /**
     * transforms socket to output stream
     * @param socket server socket
     * @return output
     */
    Observable<ObjectOutputStream> socketToPrintWriterLogic(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getOutputStream)
                .map(ObjectOutputStream::new);
    }

    /**
     * transforms socket to input stream
     * @param socket server socket
     * @return input
     */
    Observable<ObjectInputStream> socketToBufferedReaderLogic(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new);
    }

    /**
     * closes the server
     */
    private void shutdown() {
        acceptConnections = false;
    }

    /**
     * handles connection errors
     * @param error error
     */
    private void handleError(Throwable error) {
        if (error instanceof ConnectError) {
            Socket socket = ((ConnectError) error).getSocket();
            disposables.get(socket.hashCode()).dispose();
            disposables.remove(socket.hashCode());
            drawingDisposables.get(socket.hashCode()).dispose();
            drawingDisposables.remove(socket.hashCode());
        }
    }
}
