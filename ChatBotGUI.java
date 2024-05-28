import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;      
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatBotGUI extends JFrame {
    private JTextArea chatArea;      // for display of chat
    private JTextField inputField;    // for input
    private JButton sendButton;       // for sending

    public ChatBotGUI() {
        setTitle("AI ChatBot");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true); // Enable line wrapping
        chatArea.setWrapStyleWord(true); // Wrap at word boundaries
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scroll bar
        add(scrollPane, BorderLayout.CENTER);

        // Input field
        inputField = new JTextField();
        add(inputField, BorderLayout.SOUTH);
        inputField.setSize(600, 100);
        

        // Send button
        sendButton = new JButton("Send");
        sendButton.setSize(50 , 50);
        sendButton.addActionListener(new ActionListener() {
           
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        add(sendButton, BorderLayout.EAST);

        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        chatArea.append("You: " + message + "\n");

        // Call the chatGPT method to get the response
        String response = chatGPT(message);
        chatArea.append("AI: " + response + "\n");

        inputField.setText(""); // Clear the input field
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = ""; // API key goes here
        String model = "gpt-3.5-turbo"; 

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);                 //allows to use the connection for output and input of data.
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));   //object named in to read the response from the input to convert bytes into character.
            String inputLine;
            StringBuffer response = new StringBuffer();      // to store the output from gpt in response.
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();    // close the buff reader to release resource.

            // returns the extracted contents of the response.
            return extractContentFromResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatBotGUI();
            }
        });
    }
}
