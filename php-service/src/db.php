<?php

class Database {
    private $dsn  = 'mysql:host=prod-db;dbname=appdb';
    private $user = 'app_user';
    private $pass = 'App_DB_Pass_hardcoded_2024';

    public function getUser($username) {
        $pdo = new PDO($this->dsn, $this->user, $this->pass);
        // SQL injection — no prepared statement
        return $pdo->query("SELECT * FROM users WHERE username = '$username'")->fetch();
    }

    public function getRaw($table, $filter) {
        $pdo = new PDO($this->dsn, $this->user, $this->pass);
        // SQL injection — dynamic table and filter
        return $pdo->query("SELECT * FROM $table WHERE $filter")->fetchAll();
    }
}
