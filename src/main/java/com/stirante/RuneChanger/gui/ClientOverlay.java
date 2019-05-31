package com.stirante.RuneChanger.gui;

import com.stirante.RuneChanger.DebugConsts;
import com.stirante.RuneChanger.RuneChanger;
import com.stirante.RuneChanger.model.Champion;
import com.stirante.RuneChanger.model.RunePage;
import com.stirante.RuneChanger.util.RuneSelectedListener;
import com.stirante.RuneChanger.util.SimplePreferences;
import com.stirante.RuneChanger.util.SuggestedChampionSelectedListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ClientOverlay extends JPanel {

    public static final String BOT = "BOT";
    public static final String MID = "MID";
    public static final String TOP = "TOP";
    private final List<RunePage> pages = new ArrayList<>();
    private final Color textColor = new Color(0xc8aa6e);
    private final Color darkerTextColor = new Color(0x785928);
    private final Color backgroundColor = new Color(0x010a13);
    private final Color lightenColor = new Color(1f, 1f, 1f, 0.2f);
    private final Color dividerColor = new Color(0x1e2328);
    private final Color darkenColor = new Color(0f, 0f, 0f, 0.01f);
    private final Rectangle botButton = new Rectangle(0, 0, 0, 0);
    private final Rectangle midButton = new Rectangle(0, 0, 0, 0);
    private final Rectangle topButton = new Rectangle(0, 0, 0, 0);
    private Font font;
    private RuneSelectedListener runeSelectedListener;
    private Font mFont;
    private boolean opened = false;
    private int selectedRunePageIndex = -1;
    private int selectedChampionIndex = -1;
    private BufferedImage icon;
    private BufferedImage fake;
    private float lastX = 0f;
    private float lastY = 0f;
    private SceneType type;
    private ArrayList<Champion> lastChampions;
    private SuggestedChampionSelectedListener suggestedChampionSelectedListener;
    private float currentRuneMenuPosition = 0f;
    private float currentChampionsPosition = 0f;
    private Timer timer;

    public ClientOverlay() {
        super();
        try {
            InputStream is = getClass().getResourceAsStream("/Beaufort-Bold.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            mFont = font.deriveFont(15f);
            icon = ImageIO.read(getClass().getResourceAsStream("/images/runechanger-runeforge-icon-28x28.png"));
            if (DebugConsts.DISPLAY_FAKE) {
                fake = ImageIO.read(new File("champ select.png"));
            }
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        setBackground(new Color(0f, 0f, 0f, 0f));
        timer = new Timer(1, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
    }

    public void setRuneData(List<RunePage> pages, RuneSelectedListener runeSelectedListener) {
        this.runeSelectedListener = runeSelectedListener;
        this.pages.clear();
        this.pages.addAll(pages);
        repaint();
    }

    private static float ease(float currentPosition, float targetPosition) {
        return currentPosition + ((targetPosition - currentPosition) * 0.2f);
    }

    @Override
    public void paintComponent(Graphics g) {
        int fontSize = (int) (Constants.FONT_SIZE * getHeight());
        if (mFont.getSize() != fontSize) {
            mFont = font.deriveFont((float) fontSize);
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //fake image with champion selection for quick layout checking
        if (DebugConsts.DISPLAY_FAKE) {
            g2d.drawImage(fake, 0, 0, null);
        }
        g2d.setFont(mFont);
        g2d.setColor(textColor);

        if (type == SceneType.CHAMPION_SELECT) {
            drawRuneButton(g2d);
            drawRuneMenu(g2d);
            if (Boolean.parseBoolean(SimplePreferences.getValue("quickReplies"))) {
                drawQuickReplies(g2d);
            }
            if (Champion.areImagesReady()) {
                drawChampionSuggestions(g2d);
            }
        } else {
            if (Champion.areImagesReady() && currentChampionsPosition > 1f) {
                drawChampionSuggestions(g2d);
            }
        }
    }

    private void drawChampionSuggestions(Graphics2D g2d) {
        if (lastChampions == null) {
            return;
        }
        if (type != SceneType.CHAMPION_SELECT) {
            currentChampionsPosition = ease(currentChampionsPosition, 0f);
            if (currentChampionsPosition > 1f) {
                timer.restart();
            }
            else {
                currentChampionsPosition = 0f;
            }
        } else {
            currentChampionsPosition = ease(currentChampionsPosition, 100f);
            if (currentChampionsPosition < 99f) {
                timer.restart();
            }
            else {
                currentChampionsPosition = 100f;
            }
        }
        g2d.setColor(darkerTextColor);
        int barWidth = (int) (Constants.CHAMPION_SUGGESTION_WIDTH * getHeight());
        g2d.drawRect(getWidth() - barWidth + 1 + (int) (currentChampionsPosition / 100f * barWidth) - barWidth, 0,
                barWidth - 2, getHeight() - 1);
        g2d.setColor(backgroundColor);
        g2d.fillRect(getWidth() - barWidth + (int) (currentChampionsPosition / 100f * barWidth) - barWidth, 1,
                barWidth - 1, getHeight() - 2);
        for (int i = 0; i < lastChampions.size(); i++) {
            Champion champion = lastChampions.get(i);
            Image img = champion.getPortrait();
            int tileSize = (int) (Constants.CHAMPION_TILE_SIZE * getHeight());
            int rowSize = getHeight() / 6;
            if (selectedChampionIndex == i) {
                g2d.setColor(lightenColor);
                g2d.fillRect(getClientWidth(), rowSize * i, barWidth, rowSize);
            }
            g2d.drawImage(img,
                    (getClientWidth() + (barWidth - tileSize) / 2) +
                            (int) (currentChampionsPosition / 100f * barWidth) - barWidth,
                    (rowSize - tileSize) / 2 + (rowSize * i),
                    tileSize, tileSize, null);
            if (i >= 6) {
                break;
            }
        }
        g2d.clearRect(getClientWidth() - barWidth, 0, barWidth, getHeight());
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void drawQuickReplies(Graphics2D g2d) {
        int chatY = (int) (Constants.QUICK_CHAT_Y * getHeight());
        int chatX = (int) (Constants.QUICK_CHAT_X * getClientWidth());
        g2d.rotate(Math.toRadians(-90), chatX, chatY);

        int botWidth = g2d.getFontMetrics().stringWidth(BOT);
        int midWidth = g2d.getFontMetrics().stringWidth(MID);
        int topWidth = g2d.getFontMetrics().stringWidth(TOP);
        int bgHeight = g2d.getFontMetrics().getHeight() - (g2d.getFontMetrics().getAscent() / 2);

        g2d.setColor(darkenColor);
        g2d.fillRect(chatX, chatY - g2d.getFontMetrics().getHeight() +
                (g2d.getFontMetrics().getAscent() / 2), botWidth, bgHeight);
        botButton.x =
                (int) (Constants.QUICK_CHAT_X * (getClientWidth())) - g2d.getFontMetrics().getHeight() +
                        (g2d.getFontMetrics().getAscent() / 2);
        botButton.y = chatY - botWidth;
        botButton.width = bgHeight;
        botButton.height = botWidth;
        g2d.setColor(textColor);
        g2d.drawString(BOT, chatX, chatY);
        chatX += botWidth + Constants.MARGIN;

        g2d.setColor(darkenColor);
        g2d.fillRect(chatX, chatY - g2d.getFontMetrics().getHeight() +
                (g2d.getFontMetrics().getAscent() / 2), midWidth, bgHeight);
        midButton.x = botButton.x;
        midButton.y = chatY - botWidth - Constants.MARGIN - midWidth;
        midButton.width = bgHeight;
        midButton.height = midWidth;
        g2d.setColor(textColor);
        g2d.drawString(MID, chatX, chatY);
        chatX += midWidth + Constants.MARGIN;

        g2d.setColor(darkenColor);
        g2d.fillRect(chatX, chatY - g2d.getFontMetrics().getHeight() +
                (g2d.getFontMetrics().getAscent() / 2), topWidth, bgHeight);
        topButton.x = botButton.x;
        topButton.y = chatY - botWidth - Constants.MARGIN - midWidth - Constants.MARGIN - topWidth;
        topButton.width = bgHeight;
        topButton.height = topWidth;
        g2d.setColor(textColor);
        g2d.drawString(TOP, chatX, chatY);
    }

    private int getClientWidth() {
        return (int) (getWidth() - (Constants.CHAMPION_SUGGESTION_WIDTH * getHeight()));
    }

    private void drawRuneButton(Graphics2D g2d) {
        if (pages.size() > 0) {
            g2d.drawImage(icon, (int) ((getClientWidth()) * Constants.RUNE_BUTTON_POSITION_X), (int) (getHeight() *
                    Constants.RUNE_BUTTON_POSITION_Y), (int) (Constants.RUNE_BUTTON_SIZE * getHeight()), (int) (
                    Constants.RUNE_BUTTON_SIZE * getHeight()), null);
        }
        else {
            opened = false;
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void drawRuneMenu(Graphics2D g2d) {
        //open/close animations
        if (opened) {
            currentRuneMenuPosition = ease(currentRuneMenuPosition, 100f);
            if (currentRuneMenuPosition < 99f) {
                timer.restart();
            }
            else {
                currentRuneMenuPosition = 100f;
            }
        }
        else {
            currentRuneMenuPosition = ease(currentRuneMenuPosition, 0f);
            if (currentRuneMenuPosition > 1f) {
                timer.restart();
            }
            else {
                currentRuneMenuPosition = 0f;
            }
        }
        //positions and dimensions
        int itemWidth = (int) (Constants.RUNE_ITEM_WIDTH * (getClientWidth()));
        int itemHeight = (int) (Constants.RUNE_ITEM_HEIGHT * getHeight());
        int menuHeight = (int) (Constants.RUNE_ITEM_HEIGHT * getHeight() * pages.size());
        int menuX = (int) (Constants.RUNE_MENU_X * (getClientWidth()));
        int menuY = (int) ((Constants.RUNE_MENU_Y * getHeight()) -
                (itemHeight * pages.size() * (currentRuneMenuPosition / 100f)));

        //draw menu background
        g2d.setColor(backgroundColor);
        g2d.fillRect(menuX,
                menuY, itemWidth,
                menuHeight);
        g2d.setColor(textColor);
        g2d.drawRect(menuX, menuY, itemWidth, menuHeight);

        //draw menu items
        for (int i = 0; i < pages.size(); i++) {
            RunePage page = pages.get(i);
            int itemTop = (int) ((menuY + (i * Constants.RUNE_ITEM_HEIGHT * getHeight())));
            int itemBottom = itemTop + itemHeight;
            //highlight hovered item
            if (selectedRunePageIndex == i) {
                g2d.setColor(lightenColor);
                g2d.fillRect(1 + menuX, itemTop, itemWidth, itemHeight);
            }
            g2d.setColor(textColor);
            g2d.drawImage(page.getRunes().get(0).getImage(), menuX, itemTop, itemHeight, itemHeight, null);
            drawCenteredHorizontalString(g2d, menuX + itemHeight, itemBottom, page.getName());
            //draw dividers, except at the bottom
            if (i != pages.size() - 1) {
                g2d.setColor(dividerColor);
                g2d.drawLine(
                        1 + menuX, itemBottom,
                        (int) ((Constants.RUNE_MENU_X + Constants.RUNE_ITEM_WIDTH) * (getClientWidth())) -
                                1, itemBottom);
            }
        }
        //clear everything under menu
        g2d.clearRect(menuX, (int) (Constants.RUNE_MENU_Y * getHeight()), itemWidth + 1, 1000);
    }

    private void drawCenteredHorizontalString(Graphics2D g, int x, int bottom, String text) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        //we subtract item height, so we leave space for rune icon
        if (metrics.stringWidth(text) >
                (Constants.RUNE_ITEM_WIDTH * (getClientWidth())) - (Constants.RUNE_ITEM_HEIGHT * getHeight())) {
            while (metrics.stringWidth(text) >
                    (Constants.RUNE_ITEM_WIDTH * (getClientWidth())) - (Constants.RUNE_ITEM_HEIGHT * getHeight())) {
                text = text.substring(0, text.length() - 1);
            }
            text = text.substring(0, text.length() - 2) + "...";
        }
        g.drawString(text, x - 1, bottom - metrics.getHeight() + (metrics.getAscent() / 2));
    }

    public void mouseClicked(MouseEvent e) {
        if (DebugConsts.DISPLAY_FAKE) {
            float x = ((float) e.getX()) / ((float) (getClientWidth()));
            float y = ((float) e.getY()) / ((float) getHeight());
            if (e.getButton() == MouseEvent.BUTTON1) {
                lastX = x;
                lastY = y;
                System.out.println(x + " x " + y);
            }
            else {
                System.out.println("Distance: " + (x - lastX) + " x " + (y - lastY));
            }
        }
        if (pages.size() > 0 && e.getX() > ((getClientWidth()) * Constants.RUNE_BUTTON_POSITION_X) &&
                e.getX() < ((getClientWidth()) * Constants.RUNE_BUTTON_POSITION_X) + icon.getWidth() &&
                e.getY() > (getHeight() * Constants.RUNE_BUTTON_POSITION_Y) &&
                e.getY() < (getHeight() * Constants.RUNE_BUTTON_POSITION_Y) + icon.getHeight()) {
            opened = !opened;
        }
        else if (selectedRunePageIndex != -1 && runeSelectedListener != null) {
            runeSelectedListener.onRuneSelected(pages.get(selectedRunePageIndex));
            opened = !opened;
        }
        else if (selectedChampionIndex != -1 && suggestedChampionSelectedListener != null) {
            suggestedChampionSelectedListener.onChampionSelected(lastChampions.get(selectedChampionIndex));
        }
        else {
            if (botButton.contains(e.getX(), e.getY())) {
                RuneChanger.sendMessageToChampSelect(BOT.toLowerCase());
            }
            else if (midButton.contains(e.getX(), e.getY())) {
                RuneChanger.sendMessageToChampSelect(MID.toLowerCase());
            }
            else if (topButton.contains(e.getX(), e.getY())) {
                RuneChanger.sendMessageToChampSelect(TOP.toLowerCase());
            }
        }
        repaint();
    }

    public void mouseMoved(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        int runePageIndex;
        int championIndex;
        if (pages.size() > 0 && e.getX() > ((getClientWidth()) * Constants.RUNE_BUTTON_POSITION_X) &&
                e.getX() < ((getClientWidth()) * Constants.RUNE_BUTTON_POSITION_X) + icon.getWidth() &&
                e.getY() > (getHeight() * Constants.RUNE_BUTTON_POSITION_Y) &&
                e.getY() < (getHeight() * Constants.RUNE_BUTTON_POSITION_Y) + icon.getHeight()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        if (e.getY() > Constants.RUNE_MENU_Y * (getClientWidth()) ||
                e.getY() < (Constants.RUNE_MENU_Y - pages.size()) * getHeight()
                || e.getX() < Constants.RUNE_MENU_X * (getClientWidth()) ||
                e.getX() > (Constants.RUNE_MENU_X + Constants.RUNE_ITEM_WIDTH) * (getClientWidth())) {
            runePageIndex = -1;
        }
        else {
            runePageIndex = (int) ((e.getY() -
                    ((Constants.RUNE_MENU_Y - (Constants.RUNE_ITEM_HEIGHT * pages.size())) * getHeight())) /
                    (Constants.RUNE_ITEM_HEIGHT * getHeight()));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        if (runePageIndex != selectedRunePageIndex) {
            selectedRunePageIndex = runePageIndex;
            repaint();
        }


        if (e.getX() < getClientWidth()) {
            championIndex = -1;
        }
        else {
            championIndex = (int) ((float) e.getY() / (float) (getHeight() / 6));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        if (championIndex != selectedChampionIndex) {
            selectedChampionIndex = championIndex;
            repaint();
        }
    }

    public void mouseExited(MouseEvent e) {
        if (selectedRunePageIndex != -1) {
            selectedRunePageIndex = -1;
            repaint();
        }
        if (selectedChampionIndex != -1) {
            selectedChampionIndex = -1;
            repaint();
        }
    }

    public void setSceneType(SceneType type) {
        this.type = type;
        timer.restart();
    }

    public void setSuggestedChampions(HashSet<Champion> lastChampions, SuggestedChampionSelectedListener suggestedChampionSelectedListener) {
        if (lastChampions != null) {
            this.lastChampions = new ArrayList<>(lastChampions);
        }
        else {
            this.lastChampions = null;
        }
        this.suggestedChampionSelectedListener = suggestedChampionSelectedListener;
    }
}
