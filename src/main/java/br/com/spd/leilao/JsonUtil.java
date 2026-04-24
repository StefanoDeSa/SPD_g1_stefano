package br.com.spd.leilao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static List<Usuario> carregarUsuarios(String caminho) throws IOException {
        return mapper.readValue(new File(caminho), new TypeReference<List<Usuario>>() {});
    }

    public static void salvarHistorico(String caminho, EstadoLeilao estado) throws IOException {
        Map<String, Object> historico = new HashMap<>();
        historico.put("item", estado.getItem());
        historico.put("maiorLance", estado.getMaiorLance());
        historico.put("lances", estado.getLances());
        historico.put("encerrado", estado.isEncerrado());

        mapper.writeValue(new File(caminho), historico);
    }
}