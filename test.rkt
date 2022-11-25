#lang racket
(define red "\033[31m")
(define end "\033[0m")
(define bold-blue "\033[34;1;4")

(define (run cmd . args)
  (define-values (process out in err)
    (apply subprocess #f #f #f (find-executable-path cmd) args))
  (define output (print-port out #f))
  (define error  (print-port err #t))
  (close-input-port out)
  (close-output-port in)
  (close-input-port err)
  (subprocess-wait process)
  (values output error))

(define (open-with command target)
  (define path (find-executable-path command))
  (if path
      (system* path target)
      #f))

(define (print-errors input)
  (define lines (regexp-match (regexp "\\[error\\].*?$") input))
  (cond
    [lines
     (for-each
      (lambda (line)
        (printf "~a~n" (string-append red line end)))
      lines)
     #t] 
    [else #f]))

(define (print-port port err?)
  (when err? (printf "~a" red))
  (define (recur accum)
    (define result (read-string 1 port))
    (cond
      [(eof-object? result)
       (when err? (printf "~a" end))
       accum]
      [else
       (printf "~a" result)
       (recur (string-append accum result))]))
  (recur ""))

(define (open-browser)
  (printf "~n~a~n" (string-append bold-blue "[MAKING COVERAGE REPORT]" end))
  (define-values (output error?) (run "sbt" "coverageReport"))
  (cond
    [(non-empty-string? error?) (exit)]
    [else
     (define grepped
       (first (regexp-match (regexp "HTML.*\\[.*scoverage-report\\/index\\.html") output)))
     (when (not grepped) (displayln "error at parsing the link") (exit))
     (define link
       (regexp-replace (regexp "HTML.*\\[") grepped ""))
     (printf "~a~n" link)
     (open-with "xdg-open" link)]))

(define-values (output err) (run "sbt" "clean" "coverage" "test"))
(cond
  [(non-empty-string? err) (exit)]
  [(print-errors output) (exit)]
  [else
   (open-browser)])