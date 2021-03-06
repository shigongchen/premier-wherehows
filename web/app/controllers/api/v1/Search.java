/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package controllers.api.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.SearchDAO;
import models.DatasetColumn;
import play.Play;
import play.api.libs.json.JsValue;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.Logger;
import org.apache.commons.lang3.StringUtils;
import dao.DatasetsDAO;

import java.util.List;

public class Search extends Controller
{
    public static Result getSearchAutoComplete()
    {
        //Logger.debug("Entering v1/Search.java:getSearchAutoComplete()");
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.set("source", Json.toJson(SearchDAO.getAutoCompleteList()));

        return ok(result);
    }

    public static Result getSearchAutoCompleteForDataset()
    {
        //Logger.debug("Entering v1/Search.java:getSearchAutoCompleteForDataset()");
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.set("source", Json.toJson(SearchDAO.getAutoCompleteListForDataset()));

        return ok(result);
    }


    public static Result searchByKeyword()
    {
        //Logger.debug("Entering v1/Search.java:searchByKeyword()");
        ObjectNode result = Json.newObject();

        int page = 1;
        int size = 15;
        String keyword = request().getQueryString("keyword");
        String category = request().getQueryString("category");
        String source = request().getQueryString("source");
        String pageStr = request().getQueryString("page");
        if (StringUtils.isBlank(pageStr))
        {
            page = 1;
        }
        else
        {
            try
            {
                page = Integer.parseInt(pageStr);
            }
            catch(NumberFormatException e)
            {
                Logger.error("Dataset Controller searchByKeyword wrong page parameter. Error message: " +
                        e.getMessage());
                page = 1;
            }
        }


        String sizeStr = request().getQueryString("size");
        if (StringUtils.isBlank(sizeStr))
        {
            size = 15;
        }
        else
        {
            try
            {
                size = Integer.parseInt(sizeStr);
            }
            catch(NumberFormatException e)
            {
                Logger.error("Dataset Controller searchByKeyword wrong page parameter. Error message: " +
                        e.getMessage());
                size = 15;
            }
        }

        result.put("status", "ok");
        Boolean isDefault = false;
        if (StringUtils.isBlank(category))
        {
            category = "datasets";
        }
        if (StringUtils.isBlank(source) || source.equalsIgnoreCase("all") || source.equalsIgnoreCase("default"))
        {
            source = null;
        }

        String searchEngine = Play.application().configuration().getString(SearchDAO.WHEREHOWS_SEARCH_ENGINE__KEY);

        if (category.toLowerCase().equalsIgnoreCase("databases"))
        {
                result.set("result", SearchDAO.getPagedDbByKeyword(category, keyword, page, size));
        }
        else if (category.toLowerCase().equalsIgnoreCase("jobs"))
        {
                result.set("result", SearchDAO.getPagedJobByKeyword(category, keyword, page, size));
        }
        else if (category.toLowerCase().equalsIgnoreCase("comments"))
        {
                result.set("result", SearchDAO.getPagedCommentsByKeyword(category, keyword, page, size));

        }
        else if (category.toLowerCase().equalsIgnoreCase("all"))
        {
                result.set("result", SearchDAO.getPagedAllByKeyword(category, keyword, page, size));

        }
        else
        {
                result.set("result", SearchDAO.getPagedDatasetByKeyword(category, keyword, source, page, size));
        }

        return ok(result);
    }


}