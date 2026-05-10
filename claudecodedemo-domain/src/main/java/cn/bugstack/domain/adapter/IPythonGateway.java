package cn.bugstack.domain.adapter;

public interface IPythonGateway {

    String chat(String question, String subject, String docId);

    String upload(byte[] fileContent, String fileName, String subject);

}
