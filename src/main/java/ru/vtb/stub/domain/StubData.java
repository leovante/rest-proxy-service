package ru.vtb.stub.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StubData {
    /***
     *  {
     *      "id": "onb2",
     *      "route": "/example/path",
     *      "method": "GET",
     *      "data": {
     *          "status": 200,
     *          "headers": [
     *              {
     *                  "name": "header-data-1",
     *                  "value": "header-data-v1"
     *              },
     *              {
     *                  "name": "header-data-2",
     *                  "value": "header-data-v2"
     *              }
     *          ],
     *          "body": {
     *              "id":  1
     *          }
     *      },
     *      "error": {
     *          "status": 401,
     *          "headers": [
     *              {
     *                  "name": "header-error",
     *                  "value": "header-error-1"
     *              }
     *          ],
     *          "body": {
     *              "message": "error message"
     *          }
     *      },
     *      "validate": {
     *          аналогично data
     *      }
     *  }
     */

    private String id;
    private String route;
    private String method;
    private DataBlock data;
    private DataBlock error;
    private DataBlock validate;
}
