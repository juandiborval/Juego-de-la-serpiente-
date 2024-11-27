import javax.swing.*;                       //libreria para  proporcionar clases para la interfas grafica como el Jframe, Jpanel, JOpcionPane 
import java.awt.*;                          //incruye las clases para componentes graficos como el color dimenciones y graficos
import java.awt.event.*;                    //permite el uso de los eventos al precionar teclas 
import java.awt.image.BufferedImage;        //permite manipular imagenes 
import javax.imageio.ImageIO;               //lee y escribe imagenes  para colocarlo en la serpientes, fondo, obstaculos y manzanas
import java.io.File;                        //sirve para el manejo de archivos y excepciones de entrada/salida
import java.io.IOException;                 //sirve para el manejo de archivos y excepciones de entrada/salida
import java.util.ArrayList;                 //permite el uso de listas dinamicas
import java.util.Random;                    //genera numeros aleatorios

public class JuegoSnake extends JPanel implements ActionListener {

    //se declara variables y constantes
    
    private final int AnchoVentana = 700; //ancho de la ventana 
    private final int largoVentana = 700; //largo de la ventana 
    private final int TamañoSnake = 15;//tamaño de la serpiente del juagador 1
    private final int TamañoSnake2 = 15; //tamaño de la serpiente del juagador 2
    private final int RetrasoI = 100; //  Cantidad de delay al comer manzana en milisegundos
    private final int Decremento = 10; // Cantidad por la que se disminuirá el delay
    private final int Incremento= 5 ;// Cantidad por la que se aumentara el delay
    private ArrayList<Point> snake;
    private ArrayList<Point> snake2;
    private ArrayList<Point> obstáculos;

    private Point Manzana; 
    private Point ManzanaTocha;

    private char Movimiento;
    private char Movimiento2;

    private boolean gameOver;
    private boolean UnJugador;//opcion si elije un jugador o dos
    private Timer Timer; //Temporizador
    private Timer SegundoTimer; // Temporizador  para contar los segundos
    private int puntaje; // Variable para la puntuación
    private int puntaje2; // Variable para la puntuación
    private int tiempo; // Contador de segundos

    private BufferedImage fondo;
    private BufferedImage serpiente1;
    private BufferedImage serpiente2;
    private BufferedImage manzana;
    private BufferedImage manzanatocha;
    private BufferedImage obstaculo;

    //crea la inicializacion del juego, establece el tañano de la ventana , configura las teclas en movimiento, abre ventana de selecionar modo de juego, carga las imagenes
    public JuegoSnake() {
        //escribe la ventana
        setPreferredSize(new Dimension(AnchoVentana, largoVentana));
       
        setFocusable(true);

        agregarKeyListeners();
        SeleccionModo();
        initGame();

        // Carga las imágenes
        try {
            fondo = ImageIO.read(new File("FONDOOO.png")); 
            manzana = ImageIO.read(new File("APPLLE.png"));
            manzanatocha = ImageIO.read(new File("MANZANA MORADA.png")); 
            serpiente1 = ImageIO.read(new File("SEEEP.png")); 
            serpiente2 = ImageIO.read(new File("SEPP02.png")); 
            obstaculo = ImageIO.read(new File("obstaculos.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ventana de modo de juego. pone a elegir al usuario si jugar en modo solo o dos jugadores
    private void SeleccionModo() {
        String[] Opciones = {"Un Jugador", "Dos Jugadores"};
        int Eleccion = JOptionPane.showOptionDialog(this, "Selecciona el modo de juego", "Modo de Juego",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, Opciones, Opciones[0]);
        
            if (Eleccion == JOptionPane.CLOSED_OPTION) {
                System.exit(0); }
        
            UnJugador = (Eleccion == 0);
    }
    
    //declara el movimiento
    private void agregarKeyListeners() {
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        if (Movimiento != 'S') Movimiento = 'W';
                        break;
                    case KeyEvent.VK_S:
                        if (Movimiento != 'W') Movimiento = 'S';
                        break;
                    case KeyEvent.VK_A:
                        if (Movimiento != 'D') Movimiento = 'A';
                        break;
                    case KeyEvent.VK_D:
                        if (Movimiento != 'A') Movimiento = 'D';
                        break;
                }

                if (!UnJugador) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (Movimiento2 != 'S') Movimiento2 = 'W';
                            break;
                        case KeyEvent.VK_DOWN:
                            if (Movimiento2 != 'W') Movimiento2 = 'S';
                            break;
                        case KeyEvent.VK_LEFT:
                            if (Movimiento2 != 'D') Movimiento2 = 'A';
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (Movimiento2 != 'A') Movimiento2 = 'D';
                            break;
                    }
                }
            }
        });
    }

    //inicia las variables y conponentes del juego, incluyendo la serpiente, los obstaculos, las manzanas, el temporzador y la puntuaciones
    private void initGame() {
        if (Timer != null) {
            Timer.stop();
        }
        if (SegundoTimer != null) {
            SegundoTimer.stop();
        }
        
        //spawnea la serpiente en el punto X=35 y Y=20
        snake = new ArrayList<>();
        snake.add(new Point(35, 20));
        Movimiento = 'i';

        obstáculos = new ArrayList<>();
        generarObstáculos();
        //si el usuario escoje "dos ugadores"spawnea la segunda serpiente en el punto X=25 y Y=20
        if (!UnJugador) {
            snake2 = new ArrayList<>();
            snake2.add(new Point(25, 30));
            Movimiento2 = 'L';

            obstáculos = new ArrayList<>();
            generarObstáculos();
        }

        SpawnManzana();
        ManzanaTocha();
        gameOver = false;
        puntaje = 0; // Inicializa la puntuación en cero
        puntaje2 = 0; // Inicializa la puntuación en cero
        tiempo = 0; // Inicializa el contador de tiempo en cero
        Timer = new Timer(RetrasoI, this);
        Timer.start();

        // Inicializa el Timer para contar los segundos
        SegundoTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    tiempo++;
                    repaint(); // Redibuja la pantalla para actualizar el tiempo
                }
            }
        });
        SegundoTimer.start();
    }

    //este metodo genera obstaculos aleatoriamente dentro del marco del juego, asegurando que no coincida con las posiciones de las manzanas o las serpientes
    private void generarObstáculos() {
        Random rand = new Random();
        //bucle for para que repita el proceso de generar obstaculos 25 veces para crear 25 obstaculos
        for (int i = 0; i < 25; i++) {
            //se define los limites para que aparezca dentro del marco del juego
            int xMin = 20 / TamañoSnake; //1 Indica el límite mínimo en el eje X para la generación de obstáculos en términos de segmentos del tamaño de la serpiente.
            int yMin = 20 / TamañoSnake;
            int xMax = (AnchoVentana / TamañoSnake) - (20 / TamañoSnake) - 1; //dentro del rango 0 y 44 los obstaculos se pueden ubicar  
            int yMax = (largoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;

            //se declara una variable point para la nueva posicion del obstaculo
            Point nuevoObstaculo;
            //variable booleana para verificar la validez de la posicion
            boolean posicionvalida;

            //el bucle do - while verifica una posicion valida
            do {
                int x = rand.nextInt(xMax - xMin + 1) + xMin;
                int y = rand.nextInt(yMax - yMin + 1) + yMin;
                nuevoObstaculo = new Point(x, y);
                posicionvalida = !obstáculos.contains(nuevoObstaculo) &&
                                  !snake.contains(nuevoObstaculo) &&
                                  (snake2 == null || !snake2.contains(nuevoObstaculo)) &&
                                  !nuevoObstaculo.equals(Manzana) &&
                                  !nuevoObstaculo.equals(ManzanaTocha);
            } while (!posicionvalida);
            //añade el nuevo obstaculo a la lista de obstaculos despues de confirmar que su pocision es valida
            obstáculos.add(nuevoObstaculo);
        }
    }

    private void cambiarObstáculos() {
        obstáculos.clear();
        generarObstáculos();
    }
    //spawneo aleatorio de la manzana (roja) dentro del marco 
    private void SpawnManzana() {
        Random rand = new Random();
        int xMin = 20 / TamañoSnake;
        int yMin = 20 / TamañoSnake;
        int xMax = (AnchoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
        int yMax = (largoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
        int x = rand.nextInt(xMax - xMin + 1) + xMin;
        int y = rand.nextInt(yMax - yMin + 1) + yMin;
        Manzana = new Point(x, y);
    }
    //spawneo aleatorio de la manzanaTocha(morada) dentro del marco 
    private void ManzanaTocha() {
        Random rand = new Random();
        int xMin = 20 / TamañoSnake;
        int yMin = 20 / TamañoSnake;
        int xMax = (AnchoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
        int yMax = (largoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
        int x = rand.nextInt(xMax - xMin + 1) + xMin;
        int y = rand.nextInt(yMax - yMin + 1) + yMin;
        ManzanaTocha = new Point(x, y);
    }
    //dibuja la imagen de fondo, las serpientes, manzanas, puntuacion, el tiempo y el marco
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (fondo != null) {
            g.drawImage(fondo, 0, 0, AnchoVentana, largoVentana, null); // Dibuja la imagen de fondo
        }

        if (gameOver) {
            MenuGameOver();
        } else {
            //pinta el marco de color rojo
            g.setColor(Color.black);
            g.drawRect(20, 20, AnchoVentana - 40, largoVentana - 40);
            
            // Dibuja la cabeza de la serpiente con imagen
            if (serpiente1 != null && !snake.isEmpty()) {
            Point cabeza = snake.get(0);
            g.drawImage(serpiente1, cabeza.x * TamañoSnake, cabeza.y * TamañoSnake, TamañoSnake, TamañoSnake, null);
            }

           // Dibuja el cuerpo de la serpiente con el color aguamarina
           Color colorAguamarina = new Color(1, 179, 163); // Color aguamarina
           g.setColor(colorAguamarina);
           for (int i = 1; i < snake.size(); i++) { // Comienza desde 1 para no dibujar la cabeza
           Point p = snake.get(i);
           g.fillRect(p.x * TamañoSnake, p.y * TamañoSnake, TamañoSnake, TamañoSnake);
           }            

           // Dibuja la cabeza de la serpiente 2 
           if (!UnJugador && serpiente2 != null && !snake2.isEmpty()) {
            Point cabeza2 = snake2.get(0);
            g.drawImage(serpiente2, cabeza2.x * TamañoSnake, cabeza2.y * TamañoSnake, TamañoSnake, TamañoSnake, null);
           }

          // Dibuja el cuerpo de la serpiente 2 con la imagen
          if (!UnJugador) {
            Color colorNaranja = new Color(252, 152, 2); // Color naranja para la segunda serpiente
            g.setColor(colorNaranja);
            for (int i = 1; i < snake2.size(); i++) { // Comienza desde 1 para no dibujar la cabeza
                Point p = snake2.get(i);
                g.fillRect(p.x * TamañoSnake, p.y * TamañoSnake, TamañoSnake, TamañoSnake);
            }
          }
            
            // Dibuja los obstáculos usando la imagen
            if (obstaculo != null) {
            for (Point p : obstáculos) {
                g.drawImage(obstaculo, p.x * TamañoSnake, p.y * TamañoSnake, TamañoSnake, TamañoSnake, null);
            }
            }
           // Dibuja la manzana usando la imagen
            if (manzana != null) {
                g.drawImage(manzana, Manzana.x * TamañoSnake, Manzana.y * TamañoSnake, TamañoSnake, TamañoSnake, null);
            }
            // Dibuja la manzanaTocha usando la imagen   
            if (manzanatocha != null) {
                g.drawImage(manzanatocha, ManzanaTocha.x * TamañoSnake, ManzanaTocha.y * TamañoSnake, TamañoSnake, TamañoSnake, null);
            }
            
            // Muestra la puntuación en la esquina superior izquierda
            g.setColor(Color.BLACK);
            g.drawString("PUNTAJE: " + puntaje, 10, 10); 
            if (!UnJugador) {
                g.drawString("PUNTAJE2: " + puntaje2, 600, 10); // Muestra la puntuación del segundo jugador
            }
            // Muestra el temporizador de tiempo en la esquina superior central
            g.drawString("TIEMPO: " + tiempo + "s", 300, 10); 
        }
    }
    //temporizador que se dispara a medida que avanza el juego(mueve la serpiente, comprueba las coliciones, actualiza la interfaz grafica como el repaint(), mover() y mover2())
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            Mover();
            if (!UnJugador) {
                mover2();
            }
            checkCollision();
            if (!UnJugador) {
                checkCollision2();
            }
            checkCollisionObstáculos();
            repaint();
        } else {
            // Detiene el Timer de segundos cuando el juego termina
            SegundoTimer.stop();
        }
    }
        //gestiona el movimiento de las serpientes. cada serpiente tiene su logica para moverse en la direccion actual, al comer manzanas y crecer
    private void Mover() {
        Point Cabeza = snake.get(0);
        Point CabezaNueva = new Point(Cabeza);

        switch (Movimiento) {
            case 'W': CabezaNueva.y--; break;
            case 'S': CabezaNueva.y++; break;
            case 'A': CabezaNueva.x--; break;
            case 'D': CabezaNueva.x++; break;
        }

        if (CabezaNueva.equals(Manzana)) {
            snake.add(0, CabezaNueva);
            SpawnManzana();
            DileyManzana();
            puntaje+=10;
            if (puntaje == puntaje%100000 ) {
                cambiarObstáculos();
            }
        } else if (CabezaNueva.equals(ManzanaTocha)) {
            snake.add(0, CabezaNueva);
            ManzanaTocha();
            DileyManzanatocha();
            puntaje ++;
        } else {
            snake.add(0, CabezaNueva);
            snake.remove(snake.size() - 1);
        }
    }
    //2- 
    private void mover2() {
        Point Cabeza2 = snake2.get(0);
        Point CabezaNueva2 = new Point(Cabeza2);

        switch (Movimiento2) {
            case 'W': CabezaNueva2.y--; break;
            case 'S': CabezaNueva2.y++; break;
            case 'A': CabezaNueva2.x--; break;
            case 'D': CabezaNueva2.x++; break;
        }


        if (CabezaNueva2.equals(Manzana)) {
            snake2.add(0, CabezaNueva2);
            SpawnManzana();
            DileyManzana();
            puntaje2+=10;
            if (puntaje == puntaje%100000 ) {
                cambiarObstáculos();
            }

        }else if (CabezaNueva2.equals(ManzanaTocha)) {
            snake2.add(0, CabezaNueva2);
            ManzanaTocha();
            DileyManzanatocha();
            puntaje2 ++;
        }else {
            snake2.add(0, CabezaNueva2);
            snake2.remove(snake2.size() - 1);
        }
        
    }
    //ajusta el retraso del temporizador despues de que la serpiente coma una manzana normal o la especial, aumentando o disminuyendo la velocidad del juego
    //aumenta el retrasodel  temporizador(aumenta le velocidad del juego)
    private void DileyManzanatocha() {
        int delay = Timer.getDelay();
        if (delay > Decremento) {
            Timer.setDelay(delay - Decremento);
        }
    }
    //disminiye el retrasodel  temporizador(disminiye le velocidad del juego)
    private void DileyManzana() {
        int delay = Timer.getDelay();
        if (delay > Incremento) {
            Timer.setDelay(delay + Decremento);
        }
    }
    //verifica si la cabeza de la manzana a colicionado con el marco o con el mismo cuerpo de la serpiente
    private void checkCollision() {
        Point head = snake.get(0);
        int xMin = 20 / TamañoSnake;
        int yMin = 20 / TamañoSnake;
        int xMax = (AnchoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
        int yMax = (largoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
    
        if (head.x < xMin || head.x > xMax || head.y < yMin || head.y > yMax || snake.subList(1, snake.size()).contains(head) || checkObstáculoColicion(head)) {
            gameOver = true;
        }
    }
    //2- verifica si la cabeza de la manzana a colicionado con el marco o con el mismo cuerpo de la serpiente
    private void checkCollision2() {
        Point head2 = snake2.get(0);
        int xMin = 20 / TamañoSnake;
        int yMin = 20 / TamañoSnake;
        int xMax = (AnchoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
        int yMax = (largoVentana / TamañoSnake) - (20 / TamañoSnake) - 1;
        if (head2.x < xMin || head2.x > xMax || head2.y < yMin || head2.y > yMax || snake2.subList(1, snake2.size()).contains(head2) || checkObstáculoColicion(head2)) {
            gameOver = true;
        }
    }

    //verifica si una de las dos serpientes coliciona con un obstaculo/mismo cuerpo/marco; si es asi el juego se termina
    private void checkCollisionObstáculos() {
        if (checkObstáculoColicion(snake.get(0)) || (snake2 != null && checkObstáculoColicion(snake2.get(0)))) {
            gameOver = true;
        }
    }

    //revisa si un point (las cabezas de la serpietes) coincide con alguna de las posiciones de los obstaculo/mismo cuerpo/marco
    private boolean checkObstáculoColicion(Point cabeza) {
        for (Point obstáculo : obstáculos) {
            if (cabeza.equals(obstáculo)) {
                return true;
            }
        }
        return false;
    }

    //muestra cuando el juego se alla termiado (permitiendo al jugador si volver a jugar o salir del juego)
    private void MenuGameOver() {
        String mensaje = "Game Over\n";
        mensaje += "Puntaje: " + puntaje; // Para un jugador
        if (!UnJugador) {
            mensaje += "\nPuntaje 2: " + puntaje2; // Si hay dos jugadores
        }
        mensaje += "\n¿Quieres volver a intentar o salir?";
    
        int Opcion = JOptionPane.showOptionDialog(this,
                mensaje,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Volver a Intentar", "Salir"},
                "Volver a Intentar");
        
        if (Opcion == JOptionPane.YES_OPTION) {
            initGame();
        } else {
            System.exit(0);
        }
    }

    //crea la ventana del juego, hace visible la ventana y establece que no se pueda cambiar de tamaño
    public static void main(String[] args) {
        JFrame Ventana = new JFrame("Serpiente Game");
        JuegoSnake game = new JuegoSnake();
        Ventana.add(game);
        Ventana.pack();
        Ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        Ventana.setResizable(false);
        Ventana.setLocationRelativeTo(null); // Centra la ventana en la pantalla
        Ventana.setVisible(true);
    }
    
}

   