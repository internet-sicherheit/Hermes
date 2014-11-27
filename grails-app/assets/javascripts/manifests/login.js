/*
 * Copyright (c) 2013, 2014 Institute for Internet Security - if(is)
 *
 * This file is part of Hermes Malware Analysis System.
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl 5
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

// This is a manifest file that'll be compiled into login.js.
// 
// @author  Andreas Sekulski
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require_self

// JQuery extension
(function($) {
    
    /**
     * Authentication method
     */
    $.fn.login = function() {
        $.ajax({
           type: $(this).prop("method"),
           url: $(this).prop("action"),
           data: $(this).serialize(),
           success: function(data)
           {
              if(data.hasOwnProperty("success")){
                  location.reload();
              } else {
                  $('#login-Container').prepend(message(messageType.error, data.error));
                  $("#password").val('');
              }
           },
           error: function(xhr) {
               $('#login-Container').prepend(message(messageType.error, xhr.responseText));
               $("#password").val('');
           }
        });
    };
}(jQuery));
