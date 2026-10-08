import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.util.regex.Pattern;

/**
 * Pet Daycare Manager - "Cloud Puppy" theme.
 * Same database logic as before (age stored as total months, time_in/time_out on pets).
 */
public class Pet extends JFrame {

    // ============================================================
    // DATABASE
    // ============================================================
    private static final String URL = "jdbc:mariadb://localhost:3306/pet_daycare";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ============================================================
    // THEME
    // ============================================================
    private static final Color SKY_TOP = new Color(0xCDEBFF);
    private static final Color SKY_BOTTOM = new Color(0xFFF1F6);
    private static final Color SIDE_TOP = new Color(0xB9DDFF);
    private static final Color SIDE_BOTTOM = new Color(0xE4D6FF);

    private static final Color PINK = new Color(0xF2709C);
    private static final Color PINK_HOVER = new Color(0xFF8FB1);
    private static final Color PINK_SOFT = new Color(0xFFE0EA);
    private static final Color SKYBLUE = new Color(0x5AA9F0);
    private static final Color SKYBLUE_HOVER = new Color(0x7BBBF5);
    private static final Color LAVENDER = new Color(0xB39DFF);
    private static final Color LAVENDER_HOVER = new Color(0xC7B6FF);
    private static final Color LAV_SOFT = new Color(0xF1ECFF);
    private static final Color LAV_HEAD = new Color(0xE9E1FF);
    private static final Color MINT = new Color(0x4FC79A);
    private static final Color MINT_SOFT = new Color(0xDDF7EC);
    private static final Color BUTTER = new Color(0xFFD66B);
    private static final Color BUTTER_HOVER = new Color(0xFFE08F);
    private static final Color CORAL = new Color(0xFF7A7A);
    private static final Color CORAL_HOVER = new Color(0xFF9393);
    private static final Color TEXT = new Color(0x3B3A5A);
    private static final Color MUTED = new Color(0x8C89A8);
    private static final Color LINE = new Color(0xEADFF2);
    private static final Color WHITE = Color.WHITE;
    private static final Color CARD_FILL = new Color(255, 255, 255, 235);

    private static final Color[] AVATARS = {
            PINK, SKYBLUE, MINT, new Color(0xFFB84D), LAVENDER
    };

    private static final String FAMILY = pickFamily(
            "Nunito", "Quicksand", "Varela Round", "Comfortaa", "Segoe UI", "Arial");
    private static final Font FONT = new Font(FAMILY, Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font(FAMILY, Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font(FAMILY, Font.BOLD, 26);
    private static final Font FONT_H2 = new Font(FAMILY, Font.BOLD, 18);

    private static String pickFamily(String... names) {
        java.util.Set<String> have = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String n : names) {
            if (have.contains(n)) return n;
        }
        return "SansSerif";
    }

    // ============================================================
    // SMALL DRAWING HELPERS
    // ============================================================
    private static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    private static Color mix(Color a, Color b, float t) {
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    /** Fluffy cloud silhouette inside the box (x, y, w, h). */
    private static void cloud(Graphics2D g, float x, float y, float w, float h) {
        Area a = new Area(new RoundRectangle2D.Float(x, y + h * 0.4f, w, h * 0.6f, h * 0.6f, h * 0.6f));
        a.add(new Area(new Ellipse2D.Float(x + w * 0.12f, y + h * 0.12f, w * 0.40f, h * 0.70f)));
        a.add(new Area(new Ellipse2D.Float(x + w * 0.38f, y, w * 0.46f, h * 0.85f)));
        g.fill(a);
    }

    private static Path2D heart(float s) {
        Path2D.Float p = new Path2D.Float();
        p.moveTo(0, s * 0.5f);
        p.curveTo(-s, -s * 0.1f, -s * 0.5f, -s * 0.8f, 0, -s * 0.3f);
        p.curveTo(s * 0.5f, -s * 0.8f, s, -s * 0.1f, 0, s * 0.5f);
        p.closePath();
        return p;
    }

    /** Draws a dog or cat face centered at (cx, cy), scaled by k. */
    private static void drawAnimal(Graphics2D g, String kind, float cx, float cy, float k, boolean happy) {
        Graphics2D a = (Graphics2D) g.create();
        a.translate(cx, cy);
        a.scale(k, k);
        if ("Cat".equals(kind)) drawCatHead(a, happy); else drawDogHead(a, happy);
        a.dispose();
    }

    /** Dog head centered on (0,0). */
    private static void drawDogHead(Graphics2D g, boolean happy) {
        Color fur = new Color(0xE9B27F), ear = new Color(0xA86B4A);
        g.setColor(ear);
        for (int s = -1; s <= 1; s += 2) {
            Graphics2D e = (Graphics2D) g.create();
            e.rotate(s * 0.3, s * 34, -10);
            e.fill(new Ellipse2D.Float(s > 0 ? 22 : -46, -32, 24, 44));
            e.dispose();
        }
        g.setColor(fur);
        g.fill(new Ellipse2D.Float(-35, -33, 70, 62));
        g.setColor(WHITE);
        g.fill(new Ellipse2D.Float(-7, -31, 14, 28));
        g.fill(new Ellipse2D.Float(-19, 2, 38, 24));
        g.setColor(new Color(255, 143, 177, 150));
        g.fill(new Ellipse2D.Float(-31, 6, 10, 6));
        g.fill(new Ellipse2D.Float(21, 6, 10, 6));
        g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int s = -1; s <= 1; s += 2) {
            float ex = s * 16, ey = -4;
            g.setColor(TEXT);
            if (happy) {
                g.draw(new Arc2D.Float(ex - 6, ey - 5, 12, 10, 0, 180, Arc2D.OPEN));
            } else {
                g.fill(new Ellipse2D.Float(ex - 4.5f, ey - 6, 9, 12));
                g.setColor(WHITE);
                g.fill(new Ellipse2D.Float(ex - 0.5f, ey - 4.5f, 3.8f, 3.8f));
                g.fill(new Ellipse2D.Float(ex - 3.5f, ey + 1.5f, 2f, 2f));
            }
        }
        g.setColor(new Color(0xFF8FA8));
        g.fill(new Ellipse2D.Float(-4.5f, happy ? 15 : 16, 9, happy ? 12 : 9));
        g.setColor(TEXT);
        g.fill(new Ellipse2D.Float(-6, 4, 12, 8));
        g.setColor(new Color(255, 255, 255, 170));
        g.fill(new Ellipse2D.Float(-3.5f, 5, 4, 2));
        g.setColor(TEXT);
        g.draw(new Arc2D.Float(-9, 10, 9, 8, 180, 180, Arc2D.OPEN));
        g.draw(new Arc2D.Float(0, 10, 9, 8, 180, 180, Arc2D.OPEN));
    }

    /** Cat head centered on (0,0). */
    private static void drawCatHead(Graphics2D g, boolean happy) {
        Color fur = new Color(0xD9D4E7), dark = new Color(0xB9B1D6), pink = new Color(0xFF8FA8);
        g.setColor(fur);
        g.fillPolygon(new int[]{-34, -31, -6}, new int[]{-6, -40, -24}, 3);
        g.fillPolygon(new int[]{34, 31, 6}, new int[]{-6, -40, -24}, 3);
        g.setColor(new Color(0xFFB6C8));
        g.fillPolygon(new int[]{-29, -28, -13}, new int[]{-12, -31, -22}, 3);
        g.fillPolygon(new int[]{29, 28, 13}, new int[]{-12, -31, -22}, 3);
        g.setColor(fur);
        g.fill(new Ellipse2D.Float(-36, -27, 72, 58));
        g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(dark);
        g.draw(new Line2D.Float(0, -26, 0, -17));
        g.draw(new Line2D.Float(-8, -25, -6, -18));
        g.draw(new Line2D.Float(8, -25, 6, -18));
        g.setColor(WHITE);
        g.fill(new Ellipse2D.Float(-14, 6, 28, 18));
        g.setColor(new Color(255, 143, 177, 150));
        g.fill(new Ellipse2D.Float(-32, 8, 10, 6));
        g.fill(new Ellipse2D.Float(22, 8, 10, 6));
        for (int s = -1; s <= 1; s += 2) {
            float ex = s * 16, ey = -3;
            g.setColor(TEXT);
            if (happy) {
                g.draw(new Arc2D.Float(ex - 6, ey - 5, 12, 10, 0, 180, Arc2D.OPEN));
            } else {
                g.fill(new Ellipse2D.Float(ex - 4.5f, ey - 6, 9, 12));
                g.setColor(WHITE);
                g.fill(new Ellipse2D.Float(ex - 0.5f, ey - 4.5f, 3.8f, 3.8f));
            }
        }
        g.setColor(pink);
        g.fillPolygon(new int[]{-4, 4, 0}, new int[]{7, 7, 12}, 3);
        g.setColor(TEXT);
        g.draw(new Arc2D.Float(-7, 11, 7, 6, 180, 180, Arc2D.OPEN));
        g.draw(new Arc2D.Float(0, 11, 7, 6, 180, 180, Arc2D.OPEN));
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(59, 58, 90, 120));
        g.draw(new Line2D.Float(-22, 10, -42, 6));
        g.draw(new Line2D.Float(-22, 14, -42, 15));
        g.draw(new Line2D.Float(22, 10, 42, 6));
        g.draw(new Line2D.Float(22, 14, 42, 15));
    }

    private static Chip chip(String text, Color bg, Color fg) {
        Chip c = new Chip();
        c.setText(text);
        c.set(bg, fg);
        return c;
    }

    // ============================================================
    // STATE
    // ============================================================
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final NavButton navPets = new NavButton("Pets");
    private final NavButton navAttendance = new NavButton("Attendance");
    private final NavButton navSpecies = new NavButton("Species");
    private final JLabel statusLabel = new JLabel(" ");
    private RoundPanel bubble;
    private final Mascot mascot = new Mascot();
    private final Burst burst = new Burst();

    private final DefaultTableModel petsModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Type", "Breed", "Age", "Owner", "Phone", "Address"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c == 0 || c == 4 ? Integer.class : String.class; }
    };
    private final JTable petsTable = new JTable(petsModel);
    private TableRowSorter<DefaultTableModel> petsSorter;
    private final JTable speciesTable = new JTable(petsModel);
    private TableRowSorter<DefaultTableModel> speciesSorter;

    private final SpeciesCard dogCard = new SpeciesCard("Dog");
    private final SpeciesCard catCard = new SpeciesCard("Cat");
    private final JLabel speciesCount = new JLabel(" ");
    private final CardLayout speciesCards = new CardLayout();
    private final JPanel speciesBody = new JPanel(speciesCards);
    private final EmptyState emptyState = new EmptyState();
    private String activeSpecies = "Dog";

    private final CuteField tfName = new CuteField("e.g. Cloud");
    private final CuteField tfBreed = new CuteField("e.g. Shiwawa");
    private final JComboBox<String> cbSpecies = makeCombo(new String[]{"Dog", "Cat"});
    private final CuteField tfOwnerName = new CuteField("Owner's full name");
    private final CuteField tfOwnerPhone = new CuteField("Phone number");
    private final CuteField tfAddress = new CuteField("Street, barangay, city");
    private final CuteField tfSearch = new CuteField("Search name, breed, owner or address...");
    private final JComboBox<String> cbAgeYears = makeCombo(ageOptions(60, "yr"));
    private final JComboBox<String> cbAgeMonths = makeCombo(ageOptions(11, "mo"));
    private PillButton btnAdd, btnSave, btnIn;
    private int selectedPetId = -1;

    private final DefaultTableModel attModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Breed", "Time In", "Time Out", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
    };
    private final JTable attTable = new JTable(attModel);
    private final JLabel inDaycareLabel = new JLabel("In daycare now: 0");

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public Pet() {
        super("Pet Daycare Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 720);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(), BorderLayout.CENTER);

        burst.setVisible(true);
        setGlassPane(burst);

        ensureSpeciesColumn();
        showScreen("pets");
        refreshAll();
    }

    private void ensureSpeciesColumn() {
        String sql = "ALTER TABLE pets ADD COLUMN IF NOT EXISTS species "
                + "VARCHAR(20) NOT NULL DEFAULT 'Dog'";
        try (Connection c = connect(); Statement statement = c.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            showDbError("Could not prepare pet type", e);
        }
    }

    // ============================================================
    // SIDEBAR
    // ============================================================
    private JComponent buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = aa(g);
                g2.setPaint(new GradientPaint(0, 0, SIDE_TOP, 0, getHeight(), SIDE_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 90));
                cloud(g2, getWidth() - 100, 18, 110, 52);
                cloud(g2, -30, 330, 120, 56);
                g2.dispose();
            }
        };
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(230, 0));
        side.setBorder(new EmptyBorder(28, 18, 18, 18));

        JLabel brand = new JLabel("Pet Daycare", new PawIcon(30, PINK), SwingConstants.LEFT);
        brand.setIconTextGap(10);
        brand.setFont(new Font(FAMILY, Font.BOLD, 21));
        brand.setForeground(TEXT);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Manager");
        sub.setFont(FONT);
        sub.setForeground(MUTED);
        sub.setBorder(new EmptyBorder(0, 40, 0, 0));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);
        side.add(sub);
        side.add(Box.createVerticalStrut(30));

        navPets.addActionListener(e -> showScreen("pets"));
        navAttendance.addActionListener(e -> {
            showScreen("attendance");
            loadAttendance();
        });
        navSpecies.addActionListener(e -> showScreen("species"));
        for (NavButton b : new NavButton[]{navPets, navAttendance, navSpecies}) {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            side.add(b);
            side.add(Box.createVerticalStrut(8));
        }

        side.add(Box.createVerticalGlue());
        JPanel mascotWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        mascotWrap.setOpaque(false);
        mascotWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        mascotWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        mascotWrap.add(mascot);
        side.add(mascotWrap);
        side.add(Box.createVerticalStrut(10));

        PillButton exit = new PillButton("Exit", CORAL, CORAL_HOVER, WHITE);
        exit.setAlignmentX(Component.LEFT_ALIGNMENT);
        exit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        exit.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(this, "Close Pet Daycare Manager?",
                    "Exit", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) System.exit(0);
        });
        side.add(exit);
        return side;
    }

    // ============================================================
    // MAIN AREA
    // ============================================================
    private JComponent buildMain() {
        SkyPanel main = new SkyPanel();
        main.setLayout(new BorderLayout());
        content.setOpaque(false);
        content.add(buildPetsScreen(), "pets");
        content.add(buildAttendanceScreen(), "attendance");
        content.add(buildSpeciesScreen(), "species");
        main.add(content, BorderLayout.CENTER);

        statusLabel.setFont(FONT_BOLD);
        statusLabel.setForeground(MUTED);
        bubble = new RoundPanel(26, CARD_FILL, true);
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(new EmptyBorder(9, 20, 11, 20));
        bubble.add(statusLabel, BorderLayout.CENTER);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 28, 14, 28));
        wrap.add(bubble);
        main.add(wrap, BorderLayout.SOUTH);
        return main;
    }

    private void showScreen(String name) {
        cards.show(content, name);
        navPets.setSelectedNav(name.equals("pets"));
        navAttendance.setSelectedNav(name.equals("attendance"));
        navSpecies.setSelectedNav(name.equals("species"));
    }

    private JComponent screenHeader(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout(14, 0));
        p.setOpaque(false);
        p.add(new JLabel(new PawIcon(42, PINK)), BorderLayout.WEST);
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        JLabel a = new JLabel(title);
        a.setFont(FONT_TITLE);
        a.setForeground(TEXT);
        JLabel b = new JLabel(subtitle);
        b.setFont(FONT);
        b.setForeground(MUTED);
        t.add(a);
        t.add(b);
        p.add(t, BorderLayout.CENTER);
        return p;
    }

    // ============================================================
    // PETS SCREEN
    // ============================================================
    private JComponent buildPetsScreen() {
        JPanel screen = new JPanel(new BorderLayout(0, 18));
        screen.setOpaque(false);
        screen.setBorder(new EmptyBorder(24, 28, 8, 28));
        screen.add(screenHeader("Pets", "Register, edit and cuddle your daycare pups"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setOpaque(false);
        body.add(buildPetForm(), BorderLayout.WEST);
        body.add(buildPetsListCard(), BorderLayout.CENTER);
        screen.add(body, BorderLayout.CENTER);
        return screen;
    }

    private JComponent buildPetForm() {
        RoundPanel card = new RoundPanel(30, CARD_FILL, true);
        card.setPreferredSize(new Dimension(330, 0));
        card.setBorder(new EmptyBorder(22, 24, 24, 24));
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;

        JLabel title = new JLabel("Pet details", new PawIcon(22, PINK), SwingConstants.LEFT);
        title.setIconTextGap(8);
        title.setFont(FONT_H2);
        title.setForeground(TEXT);

        int y = 0;
        y = put(card, g, y, title, 0, 12);
        y = put(card, g, y, label("Name"), 2, 4);
        y = put(card, g, y, new Shell(tfName), 0, 8);
        JPanel typeAndBreed = new JPanel(new GridLayout(1, 2, 10, 0));
        typeAndBreed.setOpaque(false);
        typeAndBreed.add(labeled("Dog or cat", new Shell(cbSpecies)));
        typeAndBreed.add(labeled("Breed", new Shell(tfBreed)));
        y = put(card, g, y, label("Pet type and breed"), 2, 4);
        y = put(card, g, y, typeAndBreed, 0, 8);
        y = put(card, g, y, label("Owner name"), 2, 4);
        y = put(card, g, y, new Shell(tfOwnerName), 0, 8);
        y = put(card, g, y, label("Owner phone"), 2, 4);
        y = put(card, g, y, new Shell(tfOwnerPhone), 0, 8);
        y = put(card, g, y, label("Age"), 2, 4);
        y = put(card, g, y, buildAgeInput(), 0, 8);
        y = put(card, g, y, label("Address"), 2, 4);
        y = put(card, g, y, new Shell(tfAddress), 0, 14);

        btnAdd = new PillButton("Add pet", PINK, PINK_HOVER, WHITE);
        btnSave = new PillButton("Save changes", SKYBLUE, SKYBLUE_HOVER, WHITE);
        PillButton clear = new PillButton("Clear form", LAVENDER, LAVENDER_HOVER, WHITE);
        btnAdd.addActionListener(e -> addPet());
        btnSave.addActionListener(e -> updatePet());
        clear.addActionListener(e -> clearForm());
        y = put(card, g, y, btnAdd, 0, 8);
        y = put(card, g, y, btnSave, 0, 8);
        y = put(card, g, y, clear, 0, 0);

        g.gridy = y;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        card.add(Box.createGlue(), g);
        return card;
    }

    private int put(JPanel p, GridBagConstraints g, int y, Component c, int top, int bottom) {
        g.gridy = y;
        g.insets = new Insets(top, 0, bottom, 0);
        p.add(c, g);
        return y + 1;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BOLD);
        l.setForeground(TEXT);
        return l;
    }

    private JComponent buildAgeInput() {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setOpaque(false);
        p.add(labeled("Years", new Shell(cbAgeYears)));
        p.add(labeled("Months", new Shell(cbAgeMonths)));
        return p;
    }

    private JPanel labeled(String text, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(FONT.deriveFont(12f));
        l.setForeground(MUTED);
        p.add(l, BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildPetsListCard() {
        RoundPanel card = new RoundPanel(30, CARD_FILL, true);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(20, 22, 22, 22));

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        JLabel title = new JLabel("All pets");
        title.setFont(FONT_H2);
        title.setForeground(TEXT);
        top.add(title, BorderLayout.WEST);
        Shell search = new Shell(tfSearch);
        search.setPreferredSize(new Dimension(270, 42));
        top.add(search, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        tfSearch.getDocument().addDocumentListener(docListener(this::applySearch));

        styleTable(petsTable);
        petsSorter = new TableRowSorter<>(petsModel);
        petsTable.setRowSorter(petsSorter);
        petsTable.getColumnModel().getColumn(1).setCellRenderer(new NameRenderer());
        petsTable.getColumnModel().getColumn(4).setCellRenderer(new AgeRenderer());
        petsTable.getColumnModel().getColumn(0).setMaxWidth(55);
        petsTable.getColumnModel().getColumn(4).setMaxWidth(110);
        petsTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        petsTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        petsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });
        petsTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && petsTable.rowAtPoint(e.getPoint()) >= 0) {
                    showPetDetails();
                }
            }
        });
        card.add(tableScroll(petsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        PillButton details = new PillButton("View details", SKYBLUE, SKYBLUE_HOVER, WHITE);
        details.addActionListener(e -> showPetDetails());
        PillButton delete = new PillButton("Delete selected", CORAL, CORAL_HOVER, WHITE);
        delete.addActionListener(e -> deletePet());
        bottom.add(details);
        bottom.add(delete);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // ============================================================
    // SPECIES SCREEN
    // ============================================================
    private JComponent buildSpeciesScreen() {
        JPanel screen = new JPanel(new BorderLayout(0, 18));
        screen.setOpaque(false);
        screen.setBorder(new EmptyBorder(24, 28, 8, 28));
        screen.add(screenHeader("Species", "Tap a card to browse dogs or cats"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);

        JPanel pick = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pick.setOpaque(false);
        dogCard.addActionListener(e -> setSpeciesFilter("Dog"));
        catCard.addActionListener(e -> setSpeciesFilter("Cat"));
        pick.add(dogCard);
        pick.add(catCard);
        body.add(pick, BorderLayout.NORTH);

        RoundPanel card = new RoundPanel(30, CARD_FILL, true);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 22, 22, 22));
        speciesCount.setFont(FONT_H2);
        speciesCount.setForeground(TEXT);
        card.add(speciesCount, BorderLayout.NORTH);

        styleTable(speciesTable);
        speciesSorter = new TableRowSorter<>(petsModel);
        speciesTable.setRowSorter(speciesSorter);
        speciesTable.getColumnModel().getColumn(1).setCellRenderer(new NameRenderer());
        speciesTable.getColumnModel().getColumn(4).setCellRenderer(new AgeRenderer());
        speciesTable.removeColumn(speciesTable.getColumnModel().getColumn(2));
        speciesTable.getColumnModel().getColumn(0).setMaxWidth(55);
        speciesTable.getColumnModel().getColumn(3).setMaxWidth(110);
        speciesTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        speciesTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        speciesBody.setOpaque(false);
        speciesBody.add(tableScroll(speciesTable), "table");
        speciesBody.add(emptyState, "empty");
        card.add(speciesBody, BorderLayout.CENTER);
        body.add(card, BorderLayout.CENTER);

        screen.add(body, BorderLayout.CENTER);
        setSpeciesFilter("Dog");
        return screen;
    }

    private void setSpeciesFilter(String species) {
        activeSpecies = species;
        if (speciesSorter != null) {
            speciesSorter.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(species) + "$", 2));
        }
        dogCard.setPicked("Dog".equals(species));
        catCard.setPicked("Cat".equals(species));
        updateSpeciesCount();
    }

    private void updateSpeciesCount() {
        int dogs = 0, cats = 0;
        for (int i = 0; i < petsModel.getRowCount(); i++) {
            String s = String.valueOf(petsModel.getValueAt(i, 2));
            if ("Dog".equals(s)) dogs++;
            else if ("Cat".equals(s)) cats++;
        }
        dogCard.setCount(dogs);
        catCard.setCount(cats);
        boolean cat = "Cat".equals(activeSpecies);
        int shown = cat ? cats : dogs;
        speciesCount.setText((cat ? "All cats" : "All dogs") + " (" + shown + ")");
        emptyState.setKind(activeSpecies);
        speciesCards.show(speciesBody, shown == 0 ? "empty" : "table");
    }

    // ============================================================
    // ATTENDANCE SCREEN
    // ============================================================
    private JComponent buildAttendanceScreen() {
        JPanel screen = new JPanel(new BorderLayout(0, 18));
        screen.setOpaque(false);
        screen.setBorder(new EmptyBorder(24, 28, 8, 28));
        screen.add(screenHeader("Attendance", "Select a pet, then time it in or out"), BorderLayout.NORTH);

        RoundPanel card = new RoundPanel(30, CARD_FILL, true);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(20, 22, 22, 22));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        RoundPanel pill = new RoundPanel(22, MINT_SOFT, false);
        pill.setLayout(new BorderLayout());
        pill.setBorder(new EmptyBorder(6, 16, 8, 16));
        inDaycareLabel.setFont(FONT_BOLD);
        inDaycareLabel.setForeground(new Color(0x1F8A5B));
        pill.add(inDaycareLabel);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(pill);
        top.add(left, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        btnIn = new PillButton("Time in", BUTTER, BUTTER_HOVER, TEXT);
        PillButton out = new PillButton("Time out", SKYBLUE, SKYBLUE_HOVER, WHITE);
        PillButton refresh = new PillButton("Refresh", LAVENDER, LAVENDER_HOVER, WHITE);
        btnIn.addActionListener(e -> timeIn());
        out.addActionListener(e -> timeOut());
        refresh.addActionListener(e -> {
            loadAttendance();
            setStatus("Attendance refreshed.", false);
        });
        actions.add(btnIn);
        actions.add(out);
        actions.add(refresh);
        top.add(actions, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        styleTable(attTable);
        attTable.setAutoCreateRowSorter(true);
        attTable.getColumnModel().getColumn(0).setMaxWidth(55);
        attTable.getColumnModel().getColumn(1).setCellRenderer(new NameRenderer());
        attTable.getColumnModel().getColumn(5).setCellRenderer(new ChipRenderer());
        card.add(tableScroll(attTable), BorderLayout.CENTER);

        screen.add(card, BorderLayout.CENTER);
        return screen;
    }

    // ============================================================
    // LOADING
    // ============================================================
    private void refreshAll() {
        loadPets();
        loadAttendance();
    }

    private void loadPets() {
        petsModel.setRowCount(0);
        String sql = "SELECT id, name, species, breed, age, owner_name, owner_phone, address "
                + "FROM pets ORDER BY id";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                petsModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                        rs.getString("species"), rs.getString("breed"), rs.getInt("age"),
                        rs.getString("owner_name"), rs.getString("owner_phone"), rs.getString("address")});
            }
            updateSpeciesCount();
            setStatus(petsModel.getRowCount() + " pet(s) loaded.", false);
        } catch (SQLException e) {
            showDbError("Could not load pets", e);
        }
    }

    private void loadAttendance() {
        attModel.setRowCount(0);
        int inside = 0;
        String sql = "SELECT id, name, breed, time_in, time_out FROM pets ORDER BY id";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String tin = rs.getString("time_in");
                String tout = rs.getString("time_out");
                String status;
                if (tin == null) {
                    status = "Not checked in";
                } else if (tout == null) {
                    status = "In daycare";
                    inside++;
                } else {
                    status = "Checked out";
                }
                attModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("breed"),
                        tin == null ? "" : trimMillis(tin), tout == null ? "" : trimMillis(tout), status});
            }
        } catch (SQLException e) {
            showDbError("Could not load attendance", e);
        }
        inDaycareLabel.setText("In daycare now: " + inside);
    }

    private static String trimMillis(String ts) {
        int dot = ts.indexOf('.');
        return dot > 0 ? ts.substring(0, dot) : ts;
    }

    // ============================================================
    // PET ACTIONS
    // ============================================================
    private void addPet() {
        if (!validateForm()) return;
        String sql = "INSERT INTO pets (name, species, breed, age, address, owner_name, owner_phone) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfName.getText().trim());
            ps.setString(2, (String) cbSpecies.getSelectedItem());
            ps.setString(3, tfBreed.getText().trim());
            ps.setInt(4, ageInMonths());
            ps.setString(5, tfAddress.getText().trim());
            ps.setString(6, tfOwnerName.getText().trim());
            ps.setString(7, tfOwnerPhone.getText().trim());
            ps.executeUpdate();
            clearForm();
            refreshAll();
            setStatus("Yay! New pet registered.", false);
            celebrate(btnAdd);
        } catch (SQLException e) {
            showDbError("Could not register pet", e);
        }
    }

    private void updatePet() {
        if (selectedPetId < 0) {
            warn("Pick a pet from the list first.", "Select a pet from the list to edit.");
            return;
        }
        if (!validateForm()) return;
        String sql = "UPDATE pets SET name = ?, species = ?, breed = ?, address = ?, age = ?, "
                + "owner_name = ?, owner_phone = ? WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tfName.getText().trim());
            ps.setString(2, (String) cbSpecies.getSelectedItem());
            ps.setString(3, tfBreed.getText().trim());
            ps.setString(4, tfAddress.getText().trim());
            ps.setInt(5, ageInMonths());
            ps.setString(6, tfOwnerName.getText().trim());
            ps.setString(7, tfOwnerPhone.getText().trim());
            ps.setInt(8, selectedPetId);
            int n = ps.executeUpdate();
            refreshAll();
            setStatus(n > 0 ? "Pet updated successfully." : "No pet found with that ID.", n == 0);
            if (n > 0) celebrate(btnSave);
        } catch (SQLException e) {
            showDbError("Could not update pet", e);
        }
    }

    private void deletePet() {
        if (selectedPetId < 0) {
            warn("Pick a pet from the list first.", "Select a pet from the list to delete.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete \"" + tfName.getText() + "\" (ID " + selectedPetId + ")? This cannot be undone.",
                "Delete pet", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM pets WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, selectedPetId);
            int n = ps.executeUpdate();
            clearForm();
            refreshAll();
            setStatus(n > 0 ? "Pet deleted." : "No pet found with that ID.", n == 0);
        } catch (SQLException e) {
            showDbError("Could not delete pet", e);
        }
    }

    private void timeIn() {
        int row = selectedAttendanceRow();
        if (row < 0) return;
        String status = (String) attModel.getValueAt(row, 5);
        String name = (String) attModel.getValueAt(row, 1);
        if (status.equals("In daycare")) {
            setStatus(name + " is already in daycare.", true);
            return;
        }
        int id = (Integer) attModel.getValueAt(row, 0);
        String sql = "UPDATE pets SET time_in = NOW(), time_out = NULL WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int n = ps.executeUpdate();
            loadAttendance();
            setStatus(n > 0 ? "Welcome, " + name + "!" : "No pet found with ID " + id, n == 0);
            if (n > 0) celebrate(btnIn);
        } catch (SQLException e) {
            showDbError("Could not record time in", e);
        }
    }

    private void timeOut() {
        int row = selectedAttendanceRow();
        if (row < 0) return;
        String status = (String) attModel.getValueAt(row, 5);
        String name = (String) attModel.getValueAt(row, 1);
        if (!status.equals("In daycare")) {
            setStatus(name + " is not in daycare, so there is nothing to time out.", true);
            return;
        }
        int id = (Integer) attModel.getValueAt(row, 0);
        String sql = "UPDATE pets SET time_out = NOW() WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int n = ps.executeUpdate();
            loadAttendance();
            setStatus(n > 0 ? "Bye bye, " + name + "! See you soon." : "No pet found with ID " + id, n == 0);
        } catch (SQLException e) {
            showDbError("Could not record time out", e);
        }
    }

    // ============================================================
    // FORM HELPERS
    // ============================================================
    private boolean validateForm() {
        if (tfName.getText().trim().isEmpty()) return fieldError("Enter the pet's name.", tfName);
        if (tfBreed.getText().trim().isEmpty()) return fieldError("Enter the pet's breed.", tfBreed);
        if (tfAddress.getText().trim().isEmpty()) return fieldError("Enter the pet's address.", tfAddress);
        if (tfOwnerName.getText().trim().isEmpty()) return fieldError("Enter the owner's name.", tfOwnerName);
        if (tfOwnerPhone.getText().trim().isEmpty()) return fieldError("Enter the owner's phone number.", tfOwnerPhone);
        return true;
    }

    private boolean fieldError(String msg, JComponent field) {
        setStatus(msg, true);
        Object s = field.getClientProperty("shell");
        if (s instanceof Shell) ((Shell) s).setError(true);
        field.requestFocusInWindow();
        return false;
    }

    private void fillFormFromSelection() {
        int viewRow = petsTable.getSelectedRow();
        if (viewRow < 0) return;
        int row = petsTable.convertRowIndexToModel(viewRow);
        selectedPetId = (Integer) petsModel.getValueAt(row, 0);
        tfName.setText(String.valueOf(petsModel.getValueAt(row, 1)));
        cbSpecies.setSelectedItem(String.valueOf(petsModel.getValueAt(row, 2)));
        tfBreed.setText(String.valueOf(petsModel.getValueAt(row, 3)));
        int months = (Integer) petsModel.getValueAt(row, 4);
        cbAgeYears.setSelectedIndex(Math.min(months / 12, 60));
        cbAgeMonths.setSelectedIndex(months % 12);
        tfOwnerName.setText(String.valueOf(petsModel.getValueAt(row, 5)));
        tfOwnerPhone.setText(String.valueOf(petsModel.getValueAt(row, 6)));
        tfAddress.setText(String.valueOf(petsModel.getValueAt(row, 7)));
        setStatus("Editing " + tfName.getText() + " (ID " + selectedPetId + ").", false);
    }

    // ============================================================
    // VIEW DETAILS POPUP
    // ============================================================
    private void showPetDetails() {
        int viewRow = petsTable.getSelectedRow();
        if (viewRow < 0) {
            warn("Select a pet first.", "Select a pet in the list to view its details.");
            return;
        }
        int row = petsTable.convertRowIndexToModel(viewRow);
        int id = (Integer) petsModel.getValueAt(row, 0);
        String name = String.valueOf(petsModel.getValueAt(row, 1));
        String species = String.valueOf(petsModel.getValueAt(row, 2));
        String breed = String.valueOf(petsModel.getValueAt(row, 3));
        int ageMonths = (Integer) petsModel.getValueAt(row, 4);
        String ownerName = String.valueOf(petsModel.getValueAt(row, 5));
        String ownerPhone = String.valueOf(petsModel.getValueAt(row, 6));
        String address = String.valueOf(petsModel.getValueAt(row, 7));

        String rawIn = null, rawOut = null;
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT time_in, time_out FROM pets WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rawIn = rs.getString("time_in");
                    rawOut = rs.getString("time_out");
                }
            }
        } catch (SQLException e) {
            showDbError("Could not load pet details", e);
            return;
        }
        String status = rawIn == null ? "Not checked in" : rawOut == null ? "In daycare" : "Checked out";
        String inText = rawIn == null ? "\u2014" : trimMillis(rawIn);
        String outText = rawIn == null ? "\u2014" : rawOut == null ? "Still in daycare" : trimMillis(rawOut);

        JDialog dialog = new JDialog(this, "Pet details", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        SkyPanel root = new SkyPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        RoundPanel card = new RoundPanel(30, CARD_FILL, true);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        // header: badge + name + chips
        JPanel head = new JPanel(new BorderLayout(16, 0));
        head.setOpaque(false);
        head.add(new AnimalBadge(species), BorderLayout.WEST);
        JPanel who = new JPanel();
        who.setOpaque(false);
        who.setLayout(new BoxLayout(who, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(FONT_TITLE);
        nameLabel.setForeground(TEXT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        boolean cat = "Cat".equals(species);
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        chips.add(chip(species + " \u2022 " + breed, cat ? new Color(0xDCEEFF) : PINK_SOFT,
                cat ? new Color(0x2F6DB5) : PINK));
        chips.add(chip("ID " + id, LAV_SOFT, new Color(0x6C4FD6)));
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (status.equals("In daycare")) statusRow.add(chip(status, MINT_SOFT, new Color(0x1F8A5B)));
        else if (status.equals("Checked out")) statusRow.add(chip(status, LAV_SOFT, new Color(0x6C4FD6)));
        else statusRow.add(chip(status, new Color(0xF3F0F7), MUTED));
        who.add(Box.createVerticalGlue());
        who.add(nameLabel);
        who.add(Box.createVerticalStrut(6));
        who.add(chips);
        who.add(Box.createVerticalStrut(6));
        who.add(statusRow);
        who.add(Box.createVerticalGlue());
        head.add(who, BorderLayout.CENTER);
        card.add(head, BorderLayout.NORTH);

        // info tiles
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.weightx = 1;
        g.insets = new Insets(5, 5, 5, 5);
        place(grid, g, 0, 0, 1, tile("Age", formatAge(ageMonths), PINK_SOFT, 1));
        place(grid, g, 1, 0, 1, tile("Owner", ownerName, LAV_SOFT, 1));
        place(grid, g, 0, 1, 2, tile("Owner phone", ownerPhone, MINT_SOFT, 1));
        place(grid, g, 0, 2, 2, tile("Address", address, new Color(0xFFF4D6), 2));
        place(grid, g, 0, 3, 1, tile("Time in", inText, new Color(0xE6F3FF), 1));
        place(grid, g, 1, 3, 1, tile("Time out", outText, new Color(0xE6F3FF), 1));
        card.add(grid, BorderLayout.CENTER);

        PillButton edit = new PillButton("Edit this pet", PINK, PINK_HOVER, WHITE);
        edit.addActionListener(e -> {
            dialog.dispose();
            showScreen("pets");
            tfName.requestFocusInWindow();
            setStatus("Editing " + name + " (ID " + id + ").", false);
        });
        PillButton close = new PillButton("Close", LAVENDER, LAVENDER_HOVER, WHITE);
        close.addActionListener(e -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(edit);
        actions.add(close);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setSize(480, 640);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void place(JPanel p, GridBagConstraints g, int x, int y, int w, JComponent c) {
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = w;
        p.add(c, g);
    }

    private JComponent tile(String label, String value, Color bg, int rows) {
        RoundPanel t = new RoundPanel(20, bg, false);
        t.setLayout(new BorderLayout(0, 3));
        t.setBorder(new EmptyBorder(10, 14, 12, 14));
        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(FONT_BOLD.deriveFont(11f));
        l.setForeground(MUTED);
        JTextArea v = new JTextArea(value, rows, 12);
        v.setFont(new Font(FAMILY, Font.BOLD, 15));
        v.setForeground(TEXT);
        v.setOpaque(false);
        v.setEditable(false);
        v.setFocusable(false);
        v.setLineWrap(true);
        v.setWrapStyleWord(true);
        v.setBorder(null);
        t.add(l, BorderLayout.NORTH);
        t.add(v, BorderLayout.CENTER);
        return t;
    }

    private void clearForm() {
        selectedPetId = -1;
        tfName.setText("");
        cbSpecies.setSelectedItem("Dog");
        tfBreed.setText("");
        tfOwnerName.setText("");
        tfOwnerPhone.setText("");
        tfAddress.setText("");
        cbAgeYears.setSelectedIndex(0);
        cbAgeMonths.setSelectedIndex(0);
        petsTable.clearSelection();
        tfName.requestFocusInWindow();
    }

    private static String[] ageOptions(int max, String unit) {
        String[] o = new String[max + 1];
        for (int i = 0; i <= max; i++) o[i] = i + " " + unit;
        return o;
    }

    private int ageInMonths() {
        return cbAgeYears.getSelectedIndex() * 12 + cbAgeMonths.getSelectedIndex();
    }

    private static String formatAge(int totalMonths) {
        int years = totalMonths / 12;
        int months = totalMonths % 12;
        if (years == 0) return months + " mo";
        if (months == 0) return years + " yr";
        return years + " yr " + months + " mo";
    }

    private void applySearch() {
        String text = tfSearch.getText().trim();
        petsSorter.setRowFilter(text.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 2, 3, 5, 6, 7));
    }

    private int selectedAttendanceRow() {
        int viewRow = attTable.getSelectedRow();
        if (viewRow < 0) {
            warn("Pick a pet in the table first.", "Select a pet in the table first.");
            return -1;
        }
        return attTable.convertRowIndexToModel(viewRow);
    }

    // ============================================================
    // STATUS / DIALOGS / CELEBRATION
    // ============================================================
    private void setStatus(String msg, boolean warning) {
        statusLabel.setText(msg);
        statusLabel.setForeground(warning ? new Color(0xC8483F) : MUTED);
        if (bubble != null) bubble.setFill(warning ? new Color(0xFFE3E3) : CARD_FILL);
    }

    private void warn(String status, String dialog) {
        setStatus(status, true);
        JOptionPane.showMessageDialog(this, dialog, "Oops", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDbError(String title, SQLException e) {
        setStatus(title + ": " + e.getMessage(), true);
        JOptionPane.showMessageDialog(this, title + ".\n\n" + e.getMessage(),
                "Database error", JOptionPane.ERROR_MESSAGE);
    }

    /** Mascot cheers and hearts/paws pop out of the given component. */
    private void celebrate(Component src) {
        mascot.cheer();
        Component gp = getGlassPane();
        Point p = src == null
                ? new Point(getWidth() / 2, getHeight() / 2)
                : SwingUtilities.convertPoint(src, src.getWidth() / 2, src.getHeight() / 2, gp);
        burst.spawn(p.x, p.y);
    }

    // ============================================================
    // TABLE STYLING
    // ============================================================
    private void styleTable(JTable t) {
        t.setFont(FONT);
        t.setForeground(TEXT);
        t.setBackground(WHITE);
        t.setRowHeight(40);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setDefaultRenderer(Object.class, new SoftRenderer());
        t.putClientProperty("hover", -1);
        t.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int r = t.rowAtPoint(e.getPoint());
                Object old = t.getClientProperty("hover");
                if (!(old instanceof Integer) || (Integer) old != r) {
                    t.putClientProperty("hover", r);
                    t.repaint();
                }
            }
        });
        t.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                t.putClientProperty("hover", -1);
                t.repaint();
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 40));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s,
                                                                     boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, false, false, r, c);
                l.setOpaque(true);
                l.setBackground(LAV_HEAD);
                l.setForeground(TEXT);
                l.setFont(FONT_BOLD);
                l.setHorizontalAlignment(SwingConstants.LEFT);
                l.setBorder(new EmptyBorder(0, 12, 0, 12));
                return l;
            }
        });
    }

    private JComponent tableScroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(null);
        sp.getViewport().setBackground(WHITE);
        sp.getVerticalScrollBar().setUI(new SoftScroll());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        sp.getVerticalScrollBar().setOpaque(false);
        JPanel corner = new JPanel();
        corner.setBackground(LAV_HEAD);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        return new RoundClip(sp, 24);
    }

    private static Color rowBg(JTable t, int r, boolean sel) {
        if (sel) return PINK_SOFT;
        Object h = t.getClientProperty("hover");
        if (h instanceof Integer && (Integer) h == r) return LAV_SOFT;
        return r % 2 == 0 ? WHITE : new Color(0xFFF9FC);
    }

    private static class SoftRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                                 boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, false, r, c);
            l.setOpaque(true);
            l.setBackground(rowBg(t, r, sel));
            l.setForeground(TEXT);
            l.setFont(FONT);
            l.setIcon(null);
            l.setBorder(new EmptyBorder(0, 12, 0, 12));
            return l;
        }
    }

    private static class NameRenderer extends SoftRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                                 boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            int id = ((Number) t.getValueAt(r, 0)).intValue();
            l.setIcon(new PawIcon(18, AVATARS[Math.abs(id) % AVATARS.length]));
            l.setIconTextGap(10);
            l.setFont(FONT_BOLD);
            return l;
        }
    }

    private static class AgeRenderer extends SoftRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                                 boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            if (v instanceof Number) l.setText(formatAge(((Number) v).intValue()));
            return l;
        }
    }

    /** Status shown as a rounded colored chip. */
    private static class ChipRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
        private final Chip chip = new Chip();

        ChipRenderer() {
            panel.setOpaque(true);
            panel.add(chip);
        }

        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                                 boolean foc, int r, int c) {
            String s = String.valueOf(v);
            panel.setBackground(rowBg(t, r, sel));
            chip.setText(s);
            if (s.equals("In daycare")) chip.set(MINT_SOFT, new Color(0x1F8A5B));
            else if (s.equals("Checked out")) chip.set(LAV_SOFT, new Color(0x6C4FD6));
            else chip.set(new Color(0xF3F0F7), MUTED);
            return panel;
        }
    }

    private static class Chip extends JLabel {
        private Color bg = LAV_SOFT;

        Chip() {
            setFont(FONT_BOLD.deriveFont(12.5f));
            setBorder(new EmptyBorder(3, 14, 4, 14));
            setOpaque(false);
        }

        void set(Color bg, Color fg) {
            this.bg = bg;
            setForeground(fg);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static DocumentListener docListener(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { r.run(); }
            public void removeUpdate(DocumentEvent e) { r.run(); }
            public void changedUpdate(DocumentEvent e) { r.run(); }
        };
    }

    // ============================================================
    // INPUTS
    // ============================================================
    private static JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> b = new JComboBox<>(items);
        b.setOpaque(false);
        b.setFont(FONT);
        b.setForeground(TEXT);
        b.setBorder(new EmptyBorder(4, 12, 4, 4));
        b.setMaximumRowCount(7);
        b.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton a = new JButton() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = aa(g);
                        g2.setColor(PINK);
                        int cx = getWidth() / 2, cy = getHeight() / 2;
                        g2.fillPolygon(new int[]{cx - 5, cx + 5, cx}, new int[]{cy - 2, cy - 2, cy + 4}, 3);
                        g2.dispose();
                    }
                };
                a.setContentAreaFilled(false);
                a.setBorderPainted(false);
                a.setFocusPainted(false);
                a.setOpaque(false);
                a.setPreferredSize(new Dimension(28, 20));
                return a;
            }

            @Override public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean f) { }
        });
        b.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object v, int i,
                                                                    boolean sel, boolean foc) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, v, i, sel, foc);
                l.setBorder(new EmptyBorder(6, 12, 6, 12));
                l.setFont(FONT);
                l.setForeground(TEXT);
                l.setBackground(sel ? PINK_SOFT : WHITE);
                l.setOpaque(i >= 0);
                return l;
            }
        });
        return b;
    }

    /** Text field with a placeholder (drawn inside a Shell). */
    private static class CuteField extends JTextField {
        private final String hint;

        CuteField(String hint) {
            this.hint = hint;
            setOpaque(false);
            setFont(FONT);
            setForeground(TEXT);
            setCaretColor(PINK);
            setSelectionColor(PINK_SOFT);
            setSelectedTextColor(TEXT);
            setBorder(new EmptyBorder(8, 14, 8, 14));
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2 = aa(g);
                g2.setColor(new Color(0xB9B5CF));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(hint, getInsets().left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    /** Pill-shaped wrapper that gives inputs a rounded look, focus ring and error state. */
    private static class Shell extends JPanel {
        private boolean focused, error;

        Shell(JComponent inner) {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(2, 6, 2, 6));
            add(inner);
            inner.putClientProperty("shell", this);
            inner.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
            });
            if (inner instanceof JTextComponent) {
                ((JTextComponent) inner).getDocument().addDocumentListener(docListener(() -> setError(false)));
            }
            if (inner instanceof JComboBox) {
                ((JComboBox<?>) inner).addActionListener(e -> setError(false));
            }
        }

        void setError(boolean e) {
            error = e;
            repaint();
        }

        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = Math.max(d.height, 42);
            return d;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int w = getWidth(), h = getHeight();
            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(error ? CORAL : focused ? PINK_HOVER : LINE);
            g2.drawRoundRect(1, 1, w - 3, h - 3, h, h);
            g2.dispose();
        }
    }

    // ============================================================
    // CONTAINERS
    // ============================================================
    /** Rounded card with a soft shadow. */
    private static class RoundPanel extends JPanel {
        private final int r;
        private final boolean shadow;
        private Color fill;

        RoundPanel(int r, Color fill, boolean shadow) {
            this.r = r;
            this.fill = fill;
            this.shadow = shadow;
            setOpaque(false);
        }

        void setFill(Color c) {
            fill = c;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int w = getWidth(), h = getHeight();
            if (shadow) {
                for (int i = 1; i <= 4; i++) {
                    g2.setColor(new Color(120, 100, 170, 11));
                    g2.fillRoundRect(3 - i, 5 - i, w - 6 + 2 * i, h - 9 + 2 * i, r + i * 2, r + i * 2);
                }
                g2.setColor(fill);
                g2.fillRoundRect(3, 2, w - 6, h - 9, r, r);
            } else {
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, w, h, r, r);
            }
            g2.dispose();
        }
    }

    /** Clips its child to rounded corners and draws an outline. */
    private static class RoundClip extends JPanel {
        private final int r;

        RoundClip(JComponent child, int r) {
            super(new BorderLayout());
            this.r = r;
            setOpaque(false);
            add(child);
        }

        @Override protected void paintChildren(Graphics g) {
            Graphics2D g2 = aa(g);
            g2.clip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
            super.paintChildren(g2);
            g2.dispose();
        }

        @Override public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = aa(g);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(LINE);
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, r, r);
            g2.dispose();
        }
    }

    /** Static sky gradient with clouds fixed in place. */
    private static class SkyPanel extends JPanel {
        private final float[][] clouds = {
                {0.05f, 0.10f, 170}, {0.55f, 0.26f, 240}, {0.85f, 0.06f, 140},
                {0.30f, 0.78f, 210}, {0.75f, 0.86f, 170}};

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            g2.setPaint(new GradientPaint(0, 0, SKY_TOP, 0, getHeight(), SKY_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 170));
            for (float[] c : clouds) {
                float cw = c[2];
                cloud(g2, c[0] * getWidth(), c[1] * getHeight(), cw, cw * 0.5f);
            }
            g2.dispose();
        }
    }

    // ============================================================
    // BUTTONS
    // ============================================================
    /** Chunky candy-style pill button with animated hover and a press-down effect. */
    private static class PillButton extends JButton {
        private final Color base, hover;
        private float t = 0;
        private boolean over;
        private final Timer anim = new Timer(16, null);

        PillButton(String text, Color base, Color hover, Color fg) {
            super(text);
            this.base = base;
            this.hover = hover;
            setFont(FONT_BOLD);
            setForeground(fg);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 22, 10, 22));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            anim.addActionListener(e -> {
                float target = over ? 1f : 0f;
                t += (target - t) * 0.3f;
                if (Math.abs(target - t) < 0.02f) {
                    t = target;
                    anim.stop();
                }
                repaint();
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { over = true; anim.start(); }
                @Override public void mouseExited(MouseEvent e) { over = false; anim.start(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int w = getWidth(), h = getHeight() - 4;
            int y = getModel().isPressed() ? 3 : 0;
            Color face = mix(base, hover, t);
            g2.setColor(face.darker());
            g2.fillRoundRect(0, 4, w, h, h, h);
            g2.setColor(face);
            g2.fillRoundRect(0, y, w, h, h, h);
            g2.setColor(new Color(255, 255, 255, 70));
            g2.fillRoundRect(h / 2, y + 3, w - h, h / 3, h / 3, h / 3);
            g2.translate(0, y - 2);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /** Sidebar navigation button with paw icon. */
    private static class NavButton extends JButton {
        private boolean selectedNav, over;
        private float t = 0;
        private final Timer anim = new Timer(16, null);

        NavButton(String text) {
            super(text);
            setIcon(new PawIcon(20, PINK));
            setIconTextGap(12);
            setFont(new Font(FAMILY, Font.BOLD, 15));
            setForeground(TEXT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(11, 18, 11, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            anim.addActionListener(e -> {
                float target = over ? 1f : 0f;
                t += (target - t) * 0.3f;
                if (Math.abs(target - t) < 0.02f) {
                    t = target;
                    anim.stop();
                }
                repaint();
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { over = true; anim.start(); }
                @Override public void mouseExited(MouseEvent e) { over = false; anim.start(); }
            });
        }

        void setSelectedNav(boolean s) {
            selectedNav = s;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int w = getWidth(), h = getHeight();
            if (selectedNav) {
                g2.setColor(new Color(120, 100, 170, 40));
                g2.fillRoundRect(0, 3, w, h - 3, h, h);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, w, h - 3, h, h);
            } else if (t > 0) {
                g2.setColor(new Color(255, 255, 255, (int) (120 * t)));
                g2.fillRoundRect(0, 0, w, h - 3, h, h);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ============================================================
    // PAW ICON, MASCOT, SPECIES CARDS, CONFETTI, SCROLLBAR
    // ============================================================
    private static class PawIcon implements Icon {
        private final int s;
        private final Color c;

        PawIcon(int s, Color c) {
            this.s = s;
            this.c = c;
        }

        @Override public int getIconWidth() { return s; }
        @Override public int getIconHeight() { return s; }

        @Override public void paintIcon(Component comp, Graphics g, int x, int y) {
            Graphics2D g2 = aa(g);
            g2.translate(x, y);
            g2.setColor(c);
            g2.fill(new Ellipse2D.Float(s * 0.22f, s * 0.50f, s * 0.56f, s * 0.44f));
            float[][] toes = {{0.04f, 0.30f}, {0.27f, 0.08f}, {0.51f, 0.08f}, {0.74f, 0.30f}};
            for (float[] t : toes) {
                g2.fill(new Ellipse2D.Float(s * t[0], s * t[1], s * 0.22f, s * 0.28f));
            }
            g2.dispose();
        }
    }

    /** Puppy sitting on a cloud. Shows a happy face for a moment when you cheer(). */
    private static class Mascot extends JComponent {
        private boolean happy;
        private final Timer calm;

        Mascot() {
            setPreferredSize(new Dimension(160, 128));
            calm = new Timer(1600, e -> {
                happy = false;
                repaint();
            });
            calm.setRepeats(false);
        }

        void cheer() {
            happy = true;
            repaint();
            calm.restart();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            float cx = getWidth() / 2f;
            Color fur = new Color(0xE9B27F);

            g2.setColor(new Color(255, 255, 255, 235));
            cloud(g2, cx - 66, 82, 132, 46);

            g2.setColor(fur);
            g2.fill(new Ellipse2D.Float(cx - 26, 76, 52, 38));
            g2.setColor(WHITE);
            g2.fill(new Ellipse2D.Float(cx - 12, 84, 24, 26));
            g2.setColor(fur);
            g2.fill(new Ellipse2D.Float(cx - 24, 102, 18, 12));
            g2.fill(new Ellipse2D.Float(cx + 6, 102, 18, 12));

            g2.setColor(SKYBLUE);
            g2.fill(new RoundRectangle2D.Float(cx - 22, 77, 44, 9, 9, 9));
            g2.setColor(BUTTER);
            g2.fill(new Ellipse2D.Float(cx - 5, 84, 10, 10));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.fill(new Ellipse2D.Float(cx - 3, 86, 3, 3));

            drawAnimal(g2, "Dog", cx, 50, 1f, happy);
            g2.dispose();
        }
    }

    /** Round badge with a dog or cat face. */
    private static class AnimalBadge extends JComponent {
        private final String kind;

        AnimalBadge(String kind) {
            this.kind = kind;
            setPreferredSize(new Dimension(88, 88));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            boolean cat = "Cat".equals(kind);
            g2.setColor(cat ? new Color(0xDCEEFF) : PINK_SOFT);
            g2.fillOval(2, 2, 84, 84);
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(WHITE);
            g2.drawOval(3, 3, 82, 82);
            drawAnimal(g2, kind, 44, 46, 0.85f, false);
            g2.dispose();
        }
    }

    /** Big clickable Dog / Cat card with a live count. */
    private static class SpeciesCard extends JButton {
        private final String kind;
        private final Color accent, soft;
        private boolean picked;
        private int count;

        SpeciesCard(String kind) {
            this.kind = kind;
            boolean dog = "Dog".equals(kind);
            accent = dog ? PINK : SKYBLUE;
            soft = dog ? PINK_SOFT : new Color(0xDCEEFF);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(260, 96));
        }

        void setCount(int c) {
            count = c;
            repaint();
        }

        void setPicked(boolean p) {
            picked = p;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int w = getWidth(), h = getHeight() - 4;
            boolean hov = getModel().isRollover();
            g2.setColor(new Color(120, 100, 170, picked ? 45 : 20));
            g2.fillRoundRect(0, 4, w, h, 30, 30);
            g2.setColor(picked ? soft : WHITE);
            g2.fillRoundRect(0, 0, w, h, 30, 30);
            g2.setStroke(new BasicStroke(picked ? 3f : 2f));
            g2.setColor(picked ? accent : hov ? mix(LINE, accent, 0.5f) : LINE);
            g2.drawRoundRect(1, 1, w - 3, h - 3, 30, 30);

            g2.setColor(picked ? WHITE : soft);
            g2.fillOval(14, (h - 64) / 2, 64, 64);
            drawAnimal(g2, kind, 46, h / 2f + 2, 0.62f, false);

            g2.setColor(TEXT);
            g2.setFont(FONT_H2);
            g2.drawString("Dog".equals(kind) ? "Dogs" : "Cats", 94, h / 2 - 6);
            Font big = new Font(FAMILY, Font.BOLD, 26);
            g2.setFont(big);
            g2.setColor(accent);
            String num = String.valueOf(count);
            g2.drawString(num, 94, h / 2 + 24);
            int nw = g2.getFontMetrics().stringWidth(num);
            g2.setFont(FONT.deriveFont(13f));
            g2.setColor(MUTED);
            g2.drawString(count == 1 ? "registered pet" : "registered pets", 94 + nw + 8, h / 2 + 24);
            g2.dispose();
        }
    }

    /** Friendly message when there are no dogs / cats yet. */
    private static class EmptyState extends JComponent {
        private String kind = "Dog";

        void setKind(String k) {
            kind = k;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            g2.setColor(WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            float cx = getWidth() / 2f, cy = getHeight() / 2f - 24;
            g2.setColor(LAV_SOFT);
            g2.fill(new Ellipse2D.Float(cx - 70, cy - 62, 140, 140));
            drawAnimal(g2, kind, cx, cy + 4, 1.2f, false);
            String title = "No " + ("Cat".equals(kind) ? "cats" : "dogs") + " yet";
            g2.setFont(FONT_H2);
            g2.setColor(TEXT);
            g2.drawString(title, cx - g2.getFontMetrics().stringWidth(title) / 2f, cy + 104);
            String hint = "Add one from the Pets page and they will show up here.";
            g2.setFont(FONT);
            g2.setColor(MUTED);
            g2.drawString(hint, cx - g2.getFontMetrics().stringWidth(hint) / 2f, cy + 128);
            g2.dispose();
        }
    }

    /** Glass pane that shows hearts and paws floating up after a success. */
    private static class Burst extends JComponent {
        private final java.util.List<float[]> ps = new java.util.ArrayList<>();
        private final Timer timer = new Timer(16, null);

        Burst() {
            setOpaque(false);
            timer.addActionListener(e -> {
                java.util.Iterator<float[]> it = ps.iterator();
                while (it.hasNext()) {
                    float[] p = it.next();
                    p[0] += p[2];
                    p[1] += p[3];
                    p[3] += 0.14f;
                    p[4] -= 0.022f;
                    if (p[4] <= 0) it.remove();
                }
                if (ps.isEmpty()) timer.stop();
                repaint();
            });
        }

        // let mouse clicks pass through to the app underneath
        @Override public boolean contains(int x, int y) { return false; }

        void spawn(int cx, int cy) {
            java.util.Random r = new java.util.Random();
            for (int i = 0; i < 18; i++) {
                double a = -Math.PI / 2 + (r.nextDouble() - 0.5) * Math.PI * 1.4;
                double sp = 3 + r.nextDouble() * 4;
                ps.add(new float[]{cx, cy, (float) (Math.cos(a) * sp), (float) (Math.sin(a) * sp),
                        1f, r.nextInt(2), r.nextInt(AVATARS.length)});
            }
            if (!timer.isRunning()) timer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            for (float[] p : ps) {
                Graphics2D g2 = aa(g);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        Math.max(0f, Math.min(1f, p[4]))));
                Color c = AVATARS[(int) p[6]];
                g2.translate(p[0], p[1]);
                if (p[5] == 0) {
                    g2.setColor(c);
                    g2.fill(heart(9));
                } else {
                    new PawIcon(16, c).paintIcon(this, g2, -8, -8);
                }
                g2.dispose();
            }
        }
    }

    private static class SoftScroll extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(0xF6B6CD);
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override protected JButton createDecreaseButton(int o) { return zero(); }
        @Override protected JButton createIncreaseButton(int o) { return zero(); }

        private JButton zero() {
            JButton b = new JButton();
            Dimension d = new Dimension(0, 0);
            b.setPreferredSize(d);
            b.setMinimumSize(d);
            b.setMaximumSize(d);
            return b;
        }

        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { }

        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) return;
            Graphics2D g2 = aa(g);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, r.width, r.width);
            g2.dispose();
        }
    }

    // ============================================================
    // MAIN
    // ============================================================
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        UIManager.put("OptionPane.background", SKY_BOTTOM);
        UIManager.put("Panel.background", SKY_BOTTOM);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT_BOLD);
        UIManager.put("OptionPane.messageForeground", TEXT);
        SwingUtilities.invokeLater(() -> new Pet().setVisible(true));
    }
}