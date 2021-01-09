graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 2
    x 100
    y 33
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 5
    prb 4
    x 80
    y 21
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 3
    prb 2
    x 94
    y 119
  ]
  edge [
    source 0
    target 3
    bandwith 212
    delay 498
  ]
  edge [
    source 1
    target 5
    bandwith 361
    delay 227
  ]
  edge [
    source 2
    target 4
    bandwith 116
    delay 412
  ]
]
