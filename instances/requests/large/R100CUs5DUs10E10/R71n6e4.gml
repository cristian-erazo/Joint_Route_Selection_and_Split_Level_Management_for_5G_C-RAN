graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 10
    prb 3
    x 95
    y 112
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 8
    prb 6
    x 114
    y 55
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 8
    prb 6
    x 57
    y 22
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 7
    prb 6
    x 22
    y 35
  ]
  edge [
    source 0
    target 4
    bandwith 257
    delay 413
  ]
  edge [
    source 0
    target 5
    bandwith 309
    delay 293
  ]
  edge [
    source 1
    target 2
    bandwith 491
    delay 283
  ]
  edge [
    source 1
    target 3
    bandwith 303
    delay 489
  ]
]
