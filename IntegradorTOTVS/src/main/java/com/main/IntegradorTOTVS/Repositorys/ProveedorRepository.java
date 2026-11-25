package com.main.IntegradorTOTVS.Repositorys;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.main.IntegradorTOTVS.DtoModels.ProveedorDTO;


import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class ProveedorRepository {
    private final SimpleJdbcCall spConsultarProveedores;
    
    public ProveedorRepository(JdbcTemplate jdbcTemplate) {
        // Configura el nombre exacto del procedimiento almacenado en SQL Server
        this.spConsultarProveedores = new SimpleJdbcCall(jdbcTemplate)
            .withSchemaName("dbo") // Cambia el esquema si usas otro distinto a dbo
            .withProcedureName("spProveedoresSincronizarTOTVSConsultara")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("pGrupoEmpresa", Types.VARCHAR),
                new SqlParameter("pEmpresa", Types.VARCHAR),
                new SqlOutParameter("pRespuesta", Types.LONGVARCHAR)
            )
            .returningResultSet("proveedores", (rs, rowNum) -> new ProveedorDTO(
                    rs.getString("TipoID"),
                    rs.getString("Identificacion"),
                    rs.getString("DV"),
                    rs.getString("PrimerNombre"),
                    rs.getString("SegundoNombre"),
                    rs.getString("PrimerApellido"),
                    rs.getString("SegundoApellido"),
                    rs.getString("RazonSocial"),
                    rs.getString("Direccion"),
                    rs.getString("Departamento"),
                    rs.getString("Municipio"),
                    rs.getString("Telefono"),
                    rs.getString("Telefono2"),
                    rs.getString("email"),
                    rs.getString("web")
            ));
    }


    @SuppressWarnings("unchecked")
    public  List<Map<String, Object>>  buscarProveedores(String grupoEmpresa, String empresa) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("pGrupoEmpresa", grupoEmpresa)
                .addValue("pEmpresa", empresa)
                .addValue("pRespuesta", null); // Inicializa el parámetro de salida como null

        Map<String, Object> resultado = spConsultarProveedores.execute(parametros);

        // 1. Verificar si el SP devolvió un mensaje de error en pRespuesta
        Object errorSp = resultado.get("pRespuesta");
        if (errorSp != null && !errorSp.toString().isBlank()) {
            throw new RuntimeException("Error desde SQL Server: " + errorSp);
        }
        
        List<Map<String, Object>> lista = (List<Map<String, Object>>) resultado.get("proveedores");
        
        return lista != null ? lista : Collections.emptyList();
    }


}
