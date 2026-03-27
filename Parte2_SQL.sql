SELECT DISTINCT c.nombre, c.apellidos
FROM cliente c
INNER JOIN inscripcion i ON i.idCliente = c.id
INNER JOIN disponibilidad d ON d.idProducto = i.idProducto
INNER JOIN visitan v ON v.idCliente = c.id
                    AND v.idSucursal = d.idSucursal
WHERE NOT EXISTS (
    SELECT 1
    FROM disponibilidad d2
    WHERE d2.idProducto = i.idProducto
    AND d2.idSucursal NOT IN (
        SELECT v2.idSucursal
        FROM visitan v2
        WHERE v2.idCliente = c.id
    )
);