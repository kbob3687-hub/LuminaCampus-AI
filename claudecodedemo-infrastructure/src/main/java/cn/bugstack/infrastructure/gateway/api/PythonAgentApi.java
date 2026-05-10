package cn.bugstack.infrastructure.gateway.api;

import cn.bugstack.infrastructure.gateway.dto.ChatRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class PythonAgentApi {

    private final RestTemplate restTemplate;

    @Value("${python.agent.url:http://localhost:8000}")
    private String pythonAgentUrl;

    public PythonAgentApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String chat(ChatRequestDTO request) {
        String url = pythonAgentUrl + "/api/chat";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("question", request.getQuestion());
        params.add("subject", request.getSubject() != null ? request.getSubject() : "");
        params.add("doc_id", request.getDocId() != null ? request.getDocId() : "");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        String response = restTemplate.postForObject(url, httpEntity, String.class);
        if (response == null) {
            return "服务暂时不可用";
        }
        return parseSSEMessage(response);
    }

    /**
     * 从 SSE 流中提取 message 事件的 data，去除 LaTeX 标记
     */
    private String parseSSEMessage(String sseResponse) {
        StringBuilder messageBuilder = new StringBuilder();
        for (String line : sseResponse.split("\n")) {
            if (line.startsWith("data: ") && !line.equals("data: ")) {
                String data = line.substring(6);
                // 跳过空 data（done 事件）
                if (data.trim().isEmpty()) {
                    continue;
                }
                messageBuilder.append(data).append("\n");
            }
        }
        String result = messageBuilder.toString().trim();
        return result.isEmpty() ? sseResponse : cleanLatex(result);
    }

    /**
     * 去除 LaTeX 标记，保留纯文本内容
     */
    private String cleanLatex(String text) {
        return text
                .replaceAll("\\\\boxed\\{([^}]*)\\}", "$1")   // \boxed{sin x + C} → sin x + C
                .replaceAll("\\[\\s*", "")                       // 去除 \[ ]
                .replaceAll("\\s*\\]", "")
                .replaceAll("\\$\\$", "")                        // 去除 $$ $$
                .replaceAll("\\\\int", "∫")                     // \int → ∫
                .replaceAll("\\\\sin", "sin")                   // \sin → sin
                .replaceAll("\\\\cos", "cos")
                .replaceAll("\\\\tan", "tan")
                .replaceAll("\\\\ln", "ln")
                .replaceAll("\\\\log", "log")
                .replaceAll("\\\\lim", "lim")
                .replaceAll("\\\\sum", "∑")
                .replaceAll("\\\\infty", "∞")
                .replaceAll("\\\\pi", "π")
                .replaceAll("\\\\theta", "θ")
                .replaceAll("\\\\alpha", "α")
                .replaceAll("\\\\beta", "β")
                .replaceAll("\\\\frac\\{([^}]*)\\}\\{([^}]*)\\}", "($1)/($2)")  // \frac{a}{b} → (a)/(b)
                .replaceAll("\\\\sqrt\\{([^}]*)\\}", "√($1)")   // \sqrt{x} → √(x)
                .replaceAll("\\\\,", " ")                        // \, → 空格
                .replaceAll("\\\\;", " ")
                .replaceAll("\\\\\\\\", "\n")                    // \\ → 换行
                .replaceAll("\\{", "(")                          // { → (
                .replaceAll("\\}", ")")                          // } → )
                .replaceAll("\\\\[a-zA-Z]+", "")                 // 其他 \xxx 命令去除
                .replaceAll("\\n{3,}", "\n\n")                   // 多个空行合并
                .trim();
    }

    public String upload(byte[] fileContent, String fileName, String subject) {
        String url = pythonAgentUrl + "/api/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.core.io.ByteArrayResource fileResource =
                new org.springframework.core.io.ByteArrayResource(fileContent) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                };

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", fileResource);
        params.add("subject", subject != null ? subject : "");

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, headers);
        String response = restTemplate.postForObject(url, httpEntity, String.class);
        return response != null ? response : "上传服务暂时不可用";
    }

}
