import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class flapyW extends JPanel implements ActionListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BIRD_SIZE = 30;
    private static final int PIPE_WIDTH = 80;
    private static final int PIPE_GAP = 200;
    private static final int GRAVITY = 2;
    private static final int FLAP_STRENGTH = -20;
    private int pipeSpeed = 5;

    private int birdY;
    private int birdVelocity;
    private List<Rectangle> pipes;
    private boolean isGameOver;
    private boolean gameStarted;
    private int score;

    public flapyW() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!gameStarted) {
                        gameStarted = true;
                    }
                    if (!isGameOver) {
                        flapBird();
                    } else {
                        restartGame();
                    }
                }
            }
        });

        initializeGame();
    }

    private void initializeGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        pipes = new ArrayList<>();
        isGameOver = false;
        gameStarted = false;
        score = 0;
        pipeSpeed = 5;

        addPipes(true); // Add initial pipes

        Timer timer = new Timer(20, this);
        timer.start();
    }

    private void restartGame() {
        initializeGame();
    }

    private void addPipes(boolean start) {
        int pipeHeight = new Random().nextInt(HEIGHT / 2) + 50;
        int gapPosition = HEIGHT - PIPE_GAP - pipeHeight;

        if (start) {
            pipes.add(new Rectangle(WIDTH, 0, PIPE_WIDTH, pipeHeight));
            pipes.add(new Rectangle(WIDTH, gapPosition, PIPE_WIDTH, HEIGHT - gapPosition));
        } else {
            pipes.add(new Rectangle(WIDTH, 0, PIPE_WIDTH, pipeHeight));
            pipes.add(new Rectangle(WIDTH, gapPosition, PIPE_WIDTH, HEIGHT - gapPosition));
        }
    }

    private void flapBird() {
        birdVelocity = FLAP_STRENGTH;
    }

    private void moveBird() {
        birdVelocity += GRAVITY;
        birdY += birdVelocity;

        if (birdY <= 0) {
            birdY = 0;
        } else if (birdY >= HEIGHT - BIRD_SIZE) {
            birdY = HEIGHT - BIRD_SIZE;
        }
    }

    private void movePipes() {
        for (int i = 0; i < pipes.size(); i++) {
            Rectangle pipe = pipes.get(i);
            pipe.x -= pipeSpeed;

            if (pipe.x + pipe.width < 0) {
                pipes.remove(pipe);
                if (pipe.y == 0) {
                    addPipes(false);
                }
            }
        }

        // Increase difficulty over time by accelerating the pipes
        if (score > 0 && score % 10 == 0) {
            pipeSpeed++;
        }
    }

    private boolean checkCollision() {
        Rectangle birdRect = new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE);
        for (Rectangle pipe : pipes) {
            if (pipe.intersects(birdRect)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) {
            if (!isGameOver) {
                moveBird();
                movePipes();
                if (checkCollision()) {
                    isGameOver = true;
                } else {
                    updateScore();
                }
                repaint();
            }
        }
    }

    private void updateScore() {
        for (Rectangle pipe : pipes) {
            if (pipe.x + pipe.width == 100) {
                score++;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw pipes
        for (Rectangle pipe : pipes) {
            g.setColor(Color.GREEN);
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // Draw bird
        g.setColor(Color.RED);
        g.fillRect(100, birdY, BIRD_SIZE, BIRD_SIZE);

        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 30);

        // Draw game over message
        if (isGameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to Restart", WIDTH / 2 - 110, HEIGHT / 2 + 40);
        }

        // Draw start message
        if (!gameStarted) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to Start", WIDTH / 2 - 90, HEIGHT / 2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        flapyW game = new flapyW();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
