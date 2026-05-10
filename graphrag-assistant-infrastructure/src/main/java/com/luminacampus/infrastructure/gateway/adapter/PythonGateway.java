package com.luminacampus.infrastructure.gateway.adapter;

import com.luminacampus.domain.adapter.IPythonGateway;
import com.luminacampus.infrastructure.gateway.api.PythonAgentApi;
import com.luminacampus.infrastructure.gateway.dto.ChatRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class PythonGateway implements IPythonGateway {

    private final PythonAgentApi pythonAgentApi;

    public PythonGateway(PythonAgentApi pythonAgentApi) {
        this.pythonAgentApi = pythonAgentApi;
    }

    @Override
    public String chat(String question, String subject, String docId) {
        ChatRequestDTO request = new ChatRequestDTO(question, subject, docId);
        return pythonAgentApi.chat(request);
    }

    @Override
    public String upload(byte[] fileContent, String fileName, String subject) {
        return pythonAgentApi.upload(fileContent, fileName, subject);
    }

}
