import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Login API returns a valid token and permissions"

    request {
        method 'POST'
        url '/api/auth/login'
        headers {
            contentType(applicationJson())
        }
        body(
            email: $(consumer(regex('.+@.+\\..+')), producer('admin@example.com')),
            password: $(consumer(regex('.{6,}')), producer('securepassword')),
            userType: $(consumer(regex('ADMIN|USER')), producer('ADMIN'))
        )
    }

    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
            success: true,
            data: [
                token      : $(producer(regex('^\\S+$'))),
                email      : $(producer(regex('.+@.+\\..+'))),
                refreshToken      : $(producer(regex('^\\S+$'))),
                permissions: [
                    [
                        id      : $(producer(regex('.+'))),
                        name    : $(producer(anyNonBlankString())),
                        userType: $(producer(regex('ADMIN|USER')))
                    ]
                ]

            ],
            statusCode: 200,

        )
    }
}
