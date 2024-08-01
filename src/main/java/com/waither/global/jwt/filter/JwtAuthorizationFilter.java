package com.waither.global.jwt.filter;

//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final RedisUtil redisUtil;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//        log.info("[*] Jwt Filter");
//        if (request.getServletPath().equals("/login")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//
//    }
//}