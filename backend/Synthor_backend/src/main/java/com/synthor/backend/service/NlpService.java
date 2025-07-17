package com.synthor.backend.service;

import com.synthor.backend.dto.DataGenerationRequest;
import org.springframework.stereotype.Service;

@Service
public class NlpService {

    /**
     * Parses a natural language query into a structured DataGenerationRequest.
     * This is where the AI model will be called in the future.
     *
     * @param query The natural language query from the user.
     * @return A structured DataGenerationRequest object.
     */
    public DataGenerationRequest parseQuery(String query) {
        // TODO: Implement the logic to call an external AI API (like Gemini)
        // For now, we will return a hardcoded dummy object for testing.

        System.out.println("AI Parsing for query: " + query);

        // This is a dummy response that simulates what the AI would return.
        DataGenerationRequest dummyRequest = new DataGenerationRequest();
        dummyRequest.setCount(3); // AI recognized the number 3

        // AI recognized "name" and "email"
        java.util.List<com.synthor.backend.dto.FieldRequest> fields = new java.util.ArrayList<>();
        
        com.synthor.backend.dto.FieldRequest nameField = new com.synthor.backend.dto.FieldRequest();
        nameField.setName("이름");
        nameField.setType("full_name");
        fields.add(nameField);

        com.synthor.backend.dto.FieldRequest emailField = new com.synthor.backend.dto.FieldRequest();
        emailField.setName("이메일");
        emailField.setType("email_address");
        fields.add(emailField);

        dummyRequest.setFields(fields);

        return dummyRequest;
    }
}
